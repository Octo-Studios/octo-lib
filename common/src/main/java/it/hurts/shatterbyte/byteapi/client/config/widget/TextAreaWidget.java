package it.hurts.shatterbyte.byteapi.client.config.widget;

import it.hurts.shatterbyte.byteapi.client.animation.Tween;
import it.hurts.shatterbyte.byteapi.client.animation.easing.EaseType;
import it.hurts.shatterbyte.byteapi.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.byteapi.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.byteapi.client.config.UIElements;
import it.hurts.shatterbyte.byteapi.module.config.ShatterConfig;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TextAreaWidget extends AbstractEntryWidget<String> implements SearchHighlightAware {
    private static final int PADDING_X = 4;
    private static final int PADDING_Y = 4;
    private static final int CURSOR_MARGIN = 2;
    private static final int SELECTION_COLOR = 0x66436cb3;
    private static final int HIGHLIGHT_COLOR = 0xffffd74a;

    Font font = Minecraft.getInstance().font;
    int cursorPos;
    int selectionPos;

    @Setter
    double visualCursorPos;

    private int scrollX = 0;
    private boolean selectingWithMouse = false;
    private String searchHighlightQuery = "";

    Tween cursorTween = Tween.create();

    @Setter
    Predicate<String> predicate = s -> true;
    private String placeholder = "";
    @Nullable
    private Runnable onBlur;

    public TextAreaWidget(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, String defaultValue, Supplier<String> getter, Consumer<String> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 200, 15);
        int endPos = this.getSafeValue().length();
        this.cursorPos = endPos;
        this.selectionPos = endPos;
        this.visualCursorPos = this.cursorPos;
        this.ensureCursorVisible();
    }

    public boolean seek(int where) {
        int oldPos = this.cursorPos;
        int newPos = Math.clamp(where, 0, this.getSafeValue().length());

        this.cursorPos = newPos;
        boolean hasChanged = oldPos != newPos;

        if (!this.isFocused()) {
            this.visualCursorPos = newPos;
            this.ensureCursorVisible();
            return hasChanged;
        }

        if (Math.abs(this.visualCursorPos - newPos) < 0.001d) {
            this.visualCursorPos = newPos;
            this.ensureCursorVisible();
            return hasChanged;
        }

        cursorTween.kill();
        cursorTween = Tween.create();
        cursorTween.tweenMethod(this::setVisualCursorPos, this.visualCursorPos, (double) newPos, 0.25)
                .setEaseType(EaseType.EASE_OUT)
                .setTransitionType(TransitionType.EXPO);
        cursorTween.start();

        this.ensureCursorVisible();
        return hasChanged;
    }

    private void moveCursorTo(int where, boolean keepSelection) {
        this.seek(where);

        if (!keepSelection) {
            this.selectionPos = this.cursorPos;
        } else {
            this.selectionPos = Math.clamp(this.selectionPos, 0, this.getSafeValue().length());
        }

        this.ensureCursorVisible();
    }

    private int getInnerWidth() {
        return Math.max(0, this.getWidth() - (PADDING_X * 2));
    }

    private int getInnerHeight() {
        return Math.max(0, this.getHeight() - (PADDING_Y * 2));
    }

    private double getCursorPixelX(String value) {
        double clamped = Math.clamp(visualCursorPos, 0.0, (double) value.length());

        int leftChars = (int) Math.floor(clamped);
        double frac = clamped - leftChars;

        double x = font.width(value.substring(0, leftChars));

        if (frac > 0.0d && leftChars < value.length()) {
            String nextChar = value.substring(leftChars, leftChars + 1);
            x += font.width(nextChar) * frac;
        }

        return x;
    }

    private int getCursorPixelXAt(String value, int cursorIndex) {
        int clamped = Math.clamp(cursorIndex, 0, value.length());
        return font.width(value.substring(0, clamped));
    }

    private void ensureCursorVisible() {
        String value = this.getSafeValue();
        int visibleWidth = this.getInnerWidth();

        if (visibleWidth <= 0) {
            this.scrollX = 0;
            return;
        }

        int textWidth = font.width(value);
        int maxScroll = Math.max(0, textWidth - visibleWidth);
        int cursorPixel = this.getCursorPixelXAt(value, this.cursorPos);

        int leftVisible = this.scrollX + CURSOR_MARGIN;
        int rightVisible = this.scrollX + visibleWidth - CURSOR_MARGIN;

        if (cursorPixel < leftVisible) {
            this.scrollX = Math.max(0, cursorPixel - CURSOR_MARGIN);
        } else if (cursorPixel > rightVisible) {
            this.scrollX = Math.min(maxScroll, cursorPixel - (visibleWidth - CURSOR_MARGIN));
        }

        this.scrollX = Math.clamp(this.scrollX, 0, maxScroll);
    }

    @Override
    protected void renderEntry(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        String value = this.getSafeValue();

        UIElements.TEXT_AREA.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        int clipX = this.getX() + PADDING_X;
        int clipY = this.getY() + PADDING_Y - 1;
        int clipW = this.getInnerWidth();
        int clipH = this.getInnerHeight() + 2;

        guiGraphics.enableScissor(clipX, clipY, clipX + clipW, clipY + clipH);

        int textX = clipX - this.scrollX;
        int textY = this.getY() + 4;
        if (value.isEmpty() && !this.isFocused() && !placeholder.isEmpty()) {
            guiGraphics.text(font, placeholder, clipX, textY, 0xff7f7f87, true);
        } else {
            renderSelection(guiGraphics, value, textX);
            guiGraphics.text(font, value, textX, textY, 0xffcccccc, true);
            renderSearchHighlight(guiGraphics, value, textX, textY);
        }

        if (this.isFocused()) {
            int cursorX = textX + (int) Math.round(getCursorPixelX(value));
            guiGraphics.verticalLine(
                    cursorX,
                    this.getY() + 2,
                    this.getY() + this.height - 3,
                    0xddffffff
            );
        }

        guiGraphics.disableScissor();
    }

    private void renderSelection(GuiGraphicsExtractor guiGraphics, String value, int textX) {
        if (!this.isFocused() || !this.hasSelection()) {
            return;
        }

        int selectionStart = this.getSelectionStart();
        int selectionEnd = this.getSelectionEnd();

        int minX = textX + this.getCursorPixelXAt(value, selectionStart);
        int maxX = textX + this.getCursorPixelXAt(value, selectionEnd);

        if (minX == maxX) {
            return;
        }

        guiGraphics.fill(
                minX,
                this.getY() + 3,
                maxX,
                this.getY() + this.getHeight() - 3,
                SELECTION_COLOR
        );
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!this.isFocused()) {
            return false;
        }

        String insert = StringUtil.filterText(event.codepointAsString());
        if (insert.isEmpty()) {
            return true;
        }

        insertText(insert);
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!this.isFocused()) {
            return false;
        }

        int key = event.key();
        boolean keepSelection = isShiftDown();
        boolean moveByWord = event.hasControlDownWithQuirk();

        if (isSelectAllShortcut(event)) {
            this.selectionPos = 0;
            this.moveCursorTo(this.getSafeValue().length(), true);
            return true;
        } else if (isCopyShortcut(event)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            return true;
        } else if (isPasteShortcut(event)) {
            String clipboard = TextFieldHelper.getClipboardContents(Minecraft.getInstance());
            insertText(clipboard);
            return true;
        } else if (isCutShortcut(event)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            replaceSelection("");
            return true;
        }

        switch (key) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                deleteFromCursor(-1, moveByWord);
                return true;
            }
            case GLFW.GLFW_KEY_DELETE -> {
                deleteFromCursor(1, moveByWord);
                return true;
            }
            case GLFW.GLFW_KEY_LEFT -> {
                if (!keepSelection && !moveByWord && hasSelection()) {
                    moveCursorTo(getSelectionStart(), false);
                } else if (moveByWord) {
                    moveCursorTo(getWordPosition(-1), keepSelection);
                } else {
                    moveCursorTo(offsetCursorByCodepoint(getSafeValue(), this.cursorPos, -1), keepSelection);
                }
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (!keepSelection && !moveByWord && hasSelection()) {
                    moveCursorTo(getSelectionEnd(), false);
                } else if (moveByWord) {
                    moveCursorTo(getWordPosition(1), keepSelection);
                } else {
                    moveCursorTo(offsetCursorByCodepoint(getSafeValue(), this.cursorPos, 1), keepSelection);
                }
                return true;
            }
            case GLFW.GLFW_KEY_HOME -> {
                moveCursorTo(0, keepSelection);
                return true;
            }
            case GLFW.GLFW_KEY_END -> {
                moveCursorTo(this.getSafeValue().length(), keepSelection);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return super.getCurrentFocusPath();
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            value = "";
        }

        super.setValue(value);
        this.seek(this.cursorPos);
        this.selectionPos = Math.clamp(this.selectionPos, 0, this.getSafeValue().length());
        if (!this.isFocused()) {
            this.selectionPos = this.cursorPos;
        }
        this.ensureCursorVisible();
    }

    @Override
    public void setFocused(boolean focused) {
        boolean wasFocused = this.isFocused();
        super.setFocused(focused);
        if (focused) {
            this.ensureCursorVisible();
        } else {
            this.selectingWithMouse = false;
            this.selectionPos = this.cursorPos;
        }

        if (wasFocused && !focused && onBlur != null) {
            onBlur.run();
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        boolean handled = super.mouseClicked(event, isDoubleClick);
        if (handled && event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.moveCursorTo(cursorFromMouse(event.x()), isShiftDown());
            this.selectingWithMouse = true;
        }

        return handled;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if (this.selectingWithMouse && event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.moveCursorTo(cursorFromMouse(event.x()), true);
            return true;
        }

        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.selectingWithMouse = false;
        }

        return super.mouseReleased(event);
    }

    public static Predicate<String> integerPredicate() {
        return s -> s.isEmpty() || s.matches("-?\\d+");
    }

    public void setPlaceholder(@Nullable String placeholder) {
        this.placeholder = placeholder == null ? "" : placeholder;
    }

    public void setOnBlur(@Nullable Runnable onBlur) {
        this.onBlur = onBlur;
    }

    @Override
    public void setSearchHighlightQuery(@Nullable String query) {
        this.searchHighlightQuery = normalizeSearchQuery(query);
    }

    private String getSafeValue() {
        String value = this.getValue();
        return value == null ? "" : value;
    }

    private int cursorFromMouse(double mouseX) {
        String value = this.getSafeValue();
        int localX = (int) Math.floor(mouseX) - (this.getX() + PADDING_X) + this.scrollX;

        if (localX <= 0) {
            return 0;
        }

        return this.font.plainSubstrByWidth(value, localX).length();
    }

    private boolean hasSelection() {
        return this.cursorPos != this.selectionPos;
    }

    private void renderSearchHighlight(GuiGraphicsExtractor guiGraphics, String value, int textX, int textY) {
        if (searchHighlightQuery.isEmpty() || value.isEmpty()) {
            return;
        }

        String lowered = value.toLowerCase(Locale.ROOT);
        int fromIndex = 0;
        while (true) {
            int index = lowered.indexOf(searchHighlightQuery, fromIndex);
            if (index < 0) {
                return;
            }

            int end = index + searchHighlightQuery.length();
            int offsetX = this.font.width(value.substring(0, index));
            String highlighted = value.substring(index, end);
            guiGraphics.text(this.font, highlighted, textX + offsetX, textY, HIGHLIGHT_COLOR, true);
            fromIndex = end;
        }
    }

    private int getSelectionStart() {
        return Math.min(this.cursorPos, this.selectionPos);
    }

    private int getSelectionEnd() {
        return Math.max(this.cursorPos, this.selectionPos);
    }

    private String getSelectedText() {
        String value = this.getSafeValue();
        return value.substring(this.getSelectionStart(), this.getSelectionEnd());
    }

    private int getWordPosition(int direction) {
        return StringSplitter.getWordPosition(this.getSafeValue(), direction, this.cursorPos, true);
    }

    private boolean replaceSelection(String replacement) {
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        int newCursorPos = start + replacement.length();
        return replaceRange(start, end, replacement, newCursorPos);
    }

    private boolean replaceRange(int start, int end, String replacement, int newCursorPos) {
        String value = this.getSafeValue();

        int clampedStart = Math.clamp(start, 0, value.length());
        int clampedEnd = Math.clamp(end, 0, value.length());
        if (clampedStart > clampedEnd) {
            int swap = clampedStart;
            clampedStart = clampedEnd;
            clampedEnd = swap;
        }

        String newString = value.substring(0, clampedStart) + replacement + value.substring(clampedEnd);
        if (!predicate.test(newString)) {
            return false;
        }

        super.setValue(newString);
        this.moveCursorTo(Math.clamp(newCursorPos, 0, newString.length()), false);
        return true;
    }

    private boolean insertText(@Nullable String text) {
        String sanitized = text == null ? "" : StringUtil.filterText(text).replace("\n", "");
        if (sanitized.isEmpty() && !hasSelection()) {
            return false;
        }

        return replaceSelection(sanitized);
    }

    private void deleteFromCursor(int direction, boolean byWord) {
        if (hasSelection()) {
            replaceSelection("");
            return;
        }

        String value = this.getSafeValue();
        if (value.isEmpty()) {
            return;
        }

        int targetPos = byWord ? getWordPosition(direction) : offsetCursorByCodepoint(value, this.cursorPos, direction);
        if (targetPos == this.cursorPos) {
            return;
        }

        int start = Math.min(this.cursorPos, targetPos);
        int end = Math.max(this.cursorPos, targetPos);
        replaceRange(start, end, "", start);
    }

    private int offsetCursorByCodepoint(String value, int cursor, int direction) {
        int clampedCursor = Math.clamp(cursor, 0, value.length());
        if (direction < 0) {
            if (clampedCursor == 0) {
                return 0;
            }

            return value.offsetByCodePoints(clampedCursor, -1);
        }

        if (direction > 0) {
            if (clampedCursor >= value.length()) {
                return value.length();
            }

            return value.offsetByCodePoints(clampedCursor, 1);
        }

        return clampedCursor;
    }

    private static boolean isShiftDown() {
        return Minecraft.getInstance().hasShiftDown();
    }

    private static boolean isControlOnlyShortcut(KeyEvent event, int key) {
        return event.key() == key && event.hasControlDownWithQuirk() && !isShiftDown();
    }

    private static boolean isSelectAllShortcut(KeyEvent event) {
        return isControlOnlyShortcut(event, GLFW.GLFW_KEY_A);
    }

    private static boolean isCopyShortcut(KeyEvent event) {
        return isControlOnlyShortcut(event, GLFW.GLFW_KEY_C);
    }

    private static boolean isPasteShortcut(KeyEvent event) {
        return isControlOnlyShortcut(event, GLFW.GLFW_KEY_V);
    }

    private static boolean isCutShortcut(KeyEvent event) {
        return isControlOnlyShortcut(event, GLFW.GLFW_KEY_X);
    }

    public static Predicate<String> floatPredicate() {
        return s -> s.isEmpty() || s.matches("-?\\d*(\\.\\d*)?");
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N parseNumber(Type type, String s) {
        if (type == int.class || type == Integer.class) {
            return (N) Integer.valueOf(s);
        }
        if (type == float.class || type == Float.class) {
            return (N) Float.valueOf(s);
        }
        if (type == double.class || type == Double.class) {
            return (N) Double.valueOf(s);
        }
        if (type == long.class || type == Long.class) {
            return (N) Long.valueOf(s);
        }
        if (type == short.class || type == Short.class) {
            return (N) Short.valueOf(s);
        }
        if (type == byte.class || type == Byte.class) {
            return (N) Byte.valueOf(s);
        }

        throw new IllegalArgumentException("unsupported number type: " + type);
    }

    public static Predicate<String> numericPredicateFor(Type type) {
        if (type == int.class || type == Integer.class ||
                type == long.class || type == Long.class ||
                type == short.class || type == Short.class ||
                type == byte.class || type == Byte.class) {
            return TextAreaWidget.integerPredicate();
        }

        return TextAreaWidget.floatPredicate();
    }

    private static String normalizeSearchQuery(@Nullable String query) {
        if (query == null) {
            return "";
        }

        return query.toLowerCase(Locale.ROOT).trim();
    }
}
