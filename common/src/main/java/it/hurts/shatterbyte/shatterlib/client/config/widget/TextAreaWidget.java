package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.animation.Tween;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.EaseType;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TextAreaWidget extends AbstractEntryWidget<String> {
    private static final int PADDING_X = 4;
    private static final int PADDING_Y = 4;
    private static final int CURSOR_MARGIN = 2;

    Font font = Minecraft.getInstance().font;
    int cursorPos;

    @Setter
    double visualCursorPos;

    private int scrollX = 0;

    Tween cursorTween = Tween.create();

    @Setter
    Predicate<String> predicate = s -> true;

    public TextAreaWidget(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, String defaultValue, Supplier<String> getter, Consumer<String> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 200, 15);
        this.cursorPos = this.getValue().length();
        this.visualCursorPos = this.cursorPos;
        this.ensureCursorVisible();
    }

    public boolean seek(int where) {
        int oldPos = this.cursorPos;
        int newPos = Math.clamp(where, 0, this.getValue().length());

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

    private int getInnerWidth() {
        return Math.max(0, this.getWidth() - (PADDING_X * 2));
    }

    private int getInnerHeight() {
        return Math.max(0, this.getHeight() - (PADDING_Y * 2));
    }

    private double getCursorPixelX(String value) {
        int cpCount = value.codePointCount(0, value.length());
        double clamped = Math.clamp(visualCursorPos, 0.0, cpCount);

        int leftCps = (int) Math.floor(clamped);
        double frac = clamped - leftCps;

        int leftIndex = value.offsetByCodePoints(0, leftCps);
        String left = value.substring(0, leftIndex);

        double x = font.width(left);

        if (frac > 0 && leftIndex < value.length()) {
            int nextIndex = value.offsetByCodePoints(leftIndex, 1);
            String nextChar = value.substring(leftIndex, nextIndex);
            x += font.width(nextChar) * frac;
        }

        return x;
    }

    private void ensureCursorVisible() {
        String value = this.getValue();
        int visibleWidth = this.getInnerWidth();

        if (visibleWidth <= 0) {
            this.scrollX = 0;
            return;
        }

        int textWidth = font.width(value);
        int maxScroll = Math.max(0, textWidth - visibleWidth);
        int cursorPixel = (int) Math.round(this.getCursorPixelX(value));

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
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        String value = this.getValue();

        UIElements.TEXT_AREA.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        int clipX = this.getX() + PADDING_X;
        int clipY = this.getY() + PADDING_Y - 1;
        int clipW = this.getInnerWidth();
        int clipH = this.getInnerHeight() + 2;

        guiGraphics.enableScissor(clipX, clipY, clipX + clipW, clipY + clipH);

        int textX = clipX - this.scrollX;
        int textY = this.getY() + 4;
        guiGraphics.drawString(font, value, textX, textY, 0xffcccccc, true);

        if (this.isFocused()) {
            int cursorX = textX + (int) Math.round(getCursorPixelX(value));
            guiGraphics.vLine(
                    cursorX,
                    this.getY() + 2,
                    this.getY() + this.height - 3,
                    0xddffffff
            );
        }

        guiGraphics.disableScissor();
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        String value = this.getValue();

        int insertIndex = value.offsetByCodePoints(0, cursorPos);
        String insert = event.codepointAsString();

        String newString =
                value.substring(0, insertIndex)
                        + insert
                        + value.substring(insertIndex);

        if (!predicate.test(newString)) {
            return true;
        }

        this.setValue(newString);
        this.seek(cursorPos + 1);
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        this.seek(cursorPos);

        if (event.isLeft()) {
            if (this.seek(this.cursorPos - 1)) {
                return true;
            }
        }

        if (event.isRight()) {
            if (this.seek(this.cursorPos + 1)) {
                return true;
            }
        }

        if (event.key() == 259) {
            if (cursorPos == 0) {
                return true;
            }

            String value = this.getValue();
            int deleteFrom = value.offsetByCodePoints(cursorPos, -1);

            String before = value.substring(0, deleteFrom);
            String after = value.substring(cursorPos);

            String newString = before + after;

            if (!predicate.test(newString)) {
                return false;
            }

            this.setValue(newString);
            this.seek(deleteFrom);
            return true;
        }

        return false;
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return super.getCurrentFocusPath();
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        this.seek(cursorPos);
        this.ensureCursorVisible();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            this.ensureCursorVisible();
        }
    }

    public static Predicate<String> integerPredicate() {
        return s -> s.isEmpty() || s.matches("-?\\d+");
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
}
