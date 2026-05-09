package it.hurts.shatterbyte.byteapi.client.config.widget;

import it.hurts.shatterbyte.byteapi.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.byteapi.client.screen.widget.Child;
import it.hurts.shatterbyte.byteapi.module.config.ShatterConfig;
import it.hurts.shatterbyte.byteapi.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.byteapi.module.config.type.annotation.Exclude;
import it.hurts.shatterbyte.byteapi.module.config.type.annotation.Name;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FieldWidget extends AbstractWidget implements ContainerEventHandler, Child<GenericObjectWidget>, DynamicallySized, PathContainerWidget {
    private static final int NAME_Y = 4;
    private static final int DESCRIPTION_Y = 14;
    private static final float DESCRIPTION_SCALE = 0.5f;
    private static final int CONTENT_PADDING = 4;
    private static final int ENTRY_DOWN_Y_BASE = 20;
    private static final int MULTILINE_ENTRY_EXTRA_SPACING = 2;
    private static final int NAME_COLOR = 0xffffffff;
    private static final int DESCRIPTION_COLOR = 0xff888888;
    private static final int HIGHLIGHT_COLOR = 0xffffd74a;

    GenericObjectWidget parent;
    GenericObjectWidget.FieldInfo info;
    protected String fieldName = "";
    AbstractEntryWidget<?> entryWidget;
    ResetFieldButtonWidget resetButton;

    Font font = Minecraft.getInstance().font;

    boolean dragging = false;
    GuiEventListener focused;
    private List<GuiEventListener> childListeners = List.of();
    private List<String> wrappedDescriptionLines = List.of();
    private String searchHighlightQuery = "";

    FieldWidget() {
        super(0, 0, 16, 16, Component.empty());
    }

    @Override
    public void repositionElements() {
        this.setWidth(this.getParent().getWidth() - 4);
        int resetX = this.width - 4 - this.resetButton.getWidth();
        boolean moveDown = false;
        if (entryWidget.getWidth() > (this.getWidth() - font.width(info.name()+": ") - 12 - resetButton.getWidth()) || entryWidget instanceof DynamicallySized) {
            moveDown = true;
        }

        if (entryWidget instanceof DynamicallySized stuffInside) {
            if (moveDown) {
                entryWidget.setWidth(this.width - 4);
            } else {
                entryWidget.setWidth(resetX - 8);
            }

            stuffInside.repositionElements();
        }

        if (moveDown) {
            entryWidget.setPosition(4, ENTRY_DOWN_Y_BASE);
        } else {
            entryWidget.setPosition(resetX - 4 - this.entryWidget.getWidth(), 4);
        }

        int resetY = 2;
        if (!moveDown && entryWidget instanceof ShatterColorWidget) {
            int centeredY = entryWidget.getLocalY() + (entryWidget.getHeight() - resetButton.getHeight()) / 2;
            resetY = Math.max(2, centeredY);
        }
        resetButton.setPosition(resetX, resetY);

        int descriptionWrapWidth = resolveDescriptionWrapWidth(moveDown);
        wrappedDescriptionLines = wrapDescriptionLines(descriptionWrapWidth);

        int multilineExtraHeight = getMultilineDescriptionExtraHeight();
        int multilineEntryExtraOffset = multilineExtraHeight > 0 ? multilineExtraHeight + MULTILINE_ENTRY_EXTRA_SPACING : 0;
        if (moveDown) {
            entryWidget.setPosition(4, ENTRY_DOWN_Y_BASE + multilineEntryExtraOffset);
        }

        int entryBottom = entryWidget.getHeight() + CONTENT_PADDING + entryWidget.getLocalY();
        int descriptionBottom = getDescriptionBottomY() + CONTENT_PADDING;
        this.setHeight(Math.max(16, Math.max(entryBottom, descriptionBottom)));
    }

    public void requestRelayout() {
        //this.repositionElements();

        if (parent != null) {
            parent.relayoutAndPropagate();
        }
    }

    @SneakyThrows
    public static @Nullable FieldWidget createFromField(ShatterConfig config, String path, Object parentObject, Field field, GenericObjectWidget parent) {
        FieldWidget fieldWidget = new FieldWidget();

        Class<?> clazz = field.getDeclaringClass();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(clazz, lookup);

        field.setAccessible(true);

        if (field.isAnnotationPresent(Exclude.class)) {
            return null;
        }

        int mods = field.getModifiers();
        if (Modifier.isTransient(mods) || Modifier.isStatic(mods)) {
            return null;
        }

        fieldWidget.fieldName = field.getName();

        String prettyName = field.isAnnotationPresent(Name.class)
                ? field.getAnnotation(Name.class).value()
                : AbstractEntryWidget.convertFromCamelCase(fieldWidget.fieldName);

        String description = field.isAnnotationPresent(Comment.class)
                ? field.getAnnotation(Comment.class).value()
                : "";

        fieldWidget.info = new GenericObjectWidget.FieldInfo(prettyName, description);
        fieldWidget.setParent(parent);

        AbstractEntryWidget<?> widget = AbstractEntryWidget.tryCreate(path, fieldWidget, config, privateLookup, field, parentObject, parent.getDefaultValue());

        if (widget == null) {
            return null;
        }

        fieldWidget.entryWidget = widget;
        fieldWidget.resetButton = new ResetFieldButtonWidget(widget);
        fieldWidget.resetButton.setParent(fieldWidget);
        fieldWidget.childListeners = List.of(fieldWidget.entryWidget, fieldWidget.resetButton);

        return fieldWidget;
    }

    @Override
    public @Nullable GenericObjectWidget getParent() {
        return parent;
    }

    @Override
    public void setParent(@Nullable GenericObjectWidget parent) {
        this.parent = parent;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        drawHighlightedString(guiGraphics, info.name(), this.getX() + CONTENT_PADDING, this.getY() + NAME_Y, NAME_COLOR);
        renderDescription(guiGraphics);
        resetButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        entryWidget.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderDescription(GuiGraphicsExtractor guiGraphics) {
        List<String> lines = wrappedDescriptionLines;
        if (lines.isEmpty()) {
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.getX() + CONTENT_PADDING, this.getY() + DESCRIPTION_Y);
        guiGraphics.pose().scale(DESCRIPTION_SCALE);

        for (int i = 0; i < lines.size(); i++) {
            drawHighlightedString(guiGraphics, lines.get(i), 0, i * font.lineHeight, DESCRIPTION_COLOR);
        }

        guiGraphics.pose().popMatrix();
    }

    private int getDescriptionBottomY() {
        int lineCount = wrappedDescriptionLines.size();
        if (lineCount == 0) {
            return NAME_Y + font.lineHeight;
        }

        int scaledLineHeight = Math.max(1, Math.round(font.lineHeight * DESCRIPTION_SCALE));
        return DESCRIPTION_Y + lineCount * scaledLineHeight;
    }

    private int getMultilineDescriptionExtraHeight() {
        int lineCount = wrappedDescriptionLines.size();
        if (lineCount <= 1) {
            return 0;
        }

        int scaledLineHeight = Math.max(1, Math.round(font.lineHeight * DESCRIPTION_SCALE));
        return (lineCount - 1) * scaledLineHeight;
    }

    private int resolveDescriptionWrapWidth(boolean moveDown) {
        int rightLimit = resetButton.getLocalX() - 4;
        if (!moveDown) {
            rightLimit = Math.min(rightLimit, entryWidget.getLocalX() - 4);
        }

        return Math.max(1, rightLimit - CONTENT_PADDING);
    }

    private List<String> wrapDescriptionLines(int availableWidth) {
        List<String> wrappedLines = new ArrayList<>();
        String description = info.description();
        if (description == null || description.isEmpty()) {
            return wrappedLines;
        }

        int unscaledWrapWidth = Math.max(1, Math.round(availableWidth / DESCRIPTION_SCALE));

        String[] explicitLines = description.split("\\n", -1);
        for (String explicitLine : explicitLines) {
            if (explicitLine.isEmpty()) {
                wrappedLines.add("");
                continue;
            }

            String remaining = explicitLine;
            while (!remaining.isEmpty()) {
                String linePart = font.plainSubstrByWidth(remaining, unscaledWrapWidth);
                if (linePart.isEmpty()) {
                    int firstCodePointEnd = remaining.offsetByCodePoints(0, 1);
                    linePart = remaining.substring(0, firstCodePointEnd);
                }

                wrappedLines.add(linePart);
                remaining = remaining.substring(linePart.length());
            }
        }

        return wrappedLines;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public List<? extends GuiEventListener> children() {
        return childListeners;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean isDragging) {
        this.dragging = isDragging;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        if (this.focused != focused) {
            if (this.focused != null) {
                this.focused.setFocused(false);
            }

            if (focused != null) {
                focused.setFocused(true);
            }

            this.focused = focused;
        }
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return ContainerEventHandler.super.nextFocusPath(event);
    }

    public void moveToTheTop() {
        if (this.parent == null) {
            return;
        }

        this.parent.renderables.remove(this);
        this.parent.renderables.addFirst(this);

        if (this.parent.getParent() instanceof PathContainerWidget widget) {
            widget.moveToTheTop();
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        boolean childHandled = ContainerEventHandler.super.mouseClicked(event, isDoubleClick);
        if (childHandled) {
            return true;
        }

        if (this.isMouseOver(event.x(), event.y())) {
            this.setFocused(null);
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        ContainerEventHandler.super.mouseReleased(event);
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        ContainerEventHandler.super.mouseDragged(event, mouseX, mouseY);
        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        ContainerEventHandler.super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        ContainerEventHandler.super.keyPressed(event);
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        ContainerEventHandler.super.keyReleased(event);
        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        ContainerEventHandler.super.charTyped(event);
        return super.charTyped(event);
    }

    @Override
    public boolean isFocused() {
        return ContainerEventHandler.super.isFocused();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) {
            return true;
        }

        return (entryWidget != null && entryWidget.isMouseOver(mouseX, mouseY))
                || (resetButton != null && resetButton.isMouseOver(mouseX, mouseY));
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.setFocused(null);
        }
    }

    @Override
    public String getPath() {
        if (parent == null) {
            return fieldName;
        }

        String parentFieldPath = parent.getFieldPath();

        if (parentFieldPath == null || parentFieldPath.isEmpty()) {
            return fieldName;
        }

        return parentFieldPath + "." + fieldName;
    }

    @Override
    public void playDownSound(SoundManager handler) {}

    void applySearchQuery(String query) {
        if (entryWidget instanceof GenericObjectWidget objectWidget) {
            objectWidget.applySearchQuery(query);
        } else if (entryWidget instanceof ListWidget<?> listWidget) {
            listWidget.applySearchQuery(query);
        } else if (entryWidget instanceof MapWidget<?> mapWidget) {
            mapWidget.applySearchQuery(query);
        }
    }

    void applySearchHighlight(String query) {
        this.searchHighlightQuery = normalizeSearchQuery(query);

        if (entryWidget instanceof GenericObjectWidget objectWidget) {
            objectWidget.applySearchHighlight(this.searchHighlightQuery);
        } else if (entryWidget instanceof ListWidget<?> listWidget) {
            listWidget.applySearchHighlight(this.searchHighlightQuery);
        } else if (entryWidget instanceof MapWidget<?> mapWidget) {
            mapWidget.applySearchHighlight(this.searchHighlightQuery);
        } else if (entryWidget instanceof SearchHighlightAware highlightAware) {
            highlightAware.setSearchHighlightQuery(this.searchHighlightQuery);
        }
    }

    boolean matchesSearchQuery(String query) {
        String normalizedQuery = normalizeSearchQuery(query);
        if (normalizedQuery.isEmpty()) {
            return true;
        }

        if (containsSearchToken(info.name(), normalizedQuery)
                || containsSearchToken(info.description(), normalizedQuery)
                || containsSearchToken(getEntryValueForSearch(), normalizedQuery)) {
            return true;
        }

        if (entryWidget instanceof GenericObjectWidget objectWidget) {
            return objectWidget.hasSearchResults();
        }

        if (entryWidget instanceof ListWidget<?> listWidget) {
            return listWidget.hasSearchResults();
        }

        if (entryWidget instanceof MapWidget<?> mapWidget) {
            return mapWidget.hasSearchResults();
        }

        return false;
    }

    void collectSearchMatches(String normalizedQuery, List<FieldWidget> matches) {
        if (normalizedQuery.isEmpty()) {
            return;
        }

        if (containsSearchToken(info.name(), normalizedQuery)
                || containsSearchToken(info.description(), normalizedQuery)
                || containsSearchToken(getEntryValueForSearch(), normalizedQuery)) {
            matches.add(this);
        }

        if (entryWidget instanceof GenericObjectWidget objectWidget) {
            objectWidget.collectSearchMatches(normalizedQuery, matches);
        } else if (entryWidget instanceof ListWidget<?> listWidget) {
            listWidget.collectSearchMatches(normalizedQuery, matches);
        } else if (entryWidget instanceof MapWidget<?> mapWidget) {
            mapWidget.collectSearchMatches(normalizedQuery, matches);
        }
    }

    private static boolean containsSearchToken(@Nullable String value, String normalizedQuery) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return value.toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }

    private @Nullable String getEntryValueForSearch() {
        if (entryWidget == null) {
            return null;
        }

        try {
            Object value = entryWidget.getValue();
            if (value == null) {
                return null;
            }

            if (value instanceof Enum<?> enumValue) {
                return enumValue.name();
            }

            return String.valueOf(value);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private void drawHighlightedString(GuiGraphicsExtractor guiGraphics, @Nullable String text, int x, int y, int baseColor) {
        String value = text == null ? "" : text;
        guiGraphics.text(font, value, x, y, baseColor, true);

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
            int offsetX = font.width(value.substring(0, index));
            String highlighted = value.substring(index, end);
            guiGraphics.text(font, highlighted, x + offsetX, y, HIGHLIGHT_COLOR, true);
            fromIndex = end;
        }
    }

    private static String normalizeSearchQuery(@Nullable String query) {
        if (query == null) {
            return "";
        }

        return query.toLowerCase(Locale.ROOT).trim();
    }
}
