package it.hurts.shatterbyte.byteapi.client.config.widget;

import it.hurts.shatterbyte.byteapi.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.byteapi.module.config.ShatterConfig;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IdentifierWidget extends AbstractEntryWidget<Identifier> implements ContainerEventHandler, PathContainerWidget, SearchHighlightAware {
    private static final int TEXT_HEIGHT = 15;

    protected final TextAreaWidget textWidget;
    private String textValue;
    private boolean syncingText;
    private boolean dragging;
    private GuiEventListener focused;

    public IdentifierWidget(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, Identifier defaultValue, Supplier<Identifier> getter, Consumer<Identifier> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 200, TEXT_HEIGHT);

        Identifier initialValue = normalizeIdentifier(this.getValue(), defaultValue);
        this.textValue = formatIdentifier(initialValue);

        this.textWidget = new TextAreaWidget(config, String.class, new Annotation[0], this, this.textValue, this::getTextValue, this::applyTypedValue);
        this.textWidget.setPlaceholder(getPlaceholder());
        this.textWidget.setPredicate(value -> value.length() <= 128);
        this.textWidget.setParent(this);
        this.textWidget.setOnBlur(this::commitOrRollbackText);

        if (this.getValue() != initialValue) {
            super.setValue(initialValue);
        }

        repositionElements();
    }

    protected String getPlaceholder() {
        return "minecraft:path";
    }

    protected int getLeadingWidth() {
        return 0;
    }

    protected int getWidgetHeight() {
        return TEXT_HEIGHT;
    }

    protected void renderLeading(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    protected @Nullable Identifier parseIdentifier(@Nullable String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        try {
            return Identifier.parse(normalized);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    protected Identifier normalizeIdentifier(@Nullable Identifier value, @Nullable Identifier fallback) {
        if (value != null) {
            return value;
        }

        if (fallback != null) {
            return fallback;
        }

        return Identifier.parse("minecraft:empty");
    }

    protected String formatIdentifier(@Nullable Identifier value) {
        return normalizeIdentifier(value, getDefaultValue()).toString();
    }

    protected final String getTextValue() {
        return textValue;
    }

    protected final void syncTextFromValue(@Nullable Identifier value) {
        syncingText = true;
        textValue = formatIdentifier(value);
        textWidget.setValue(textValue);
        syncingText = false;
    }

    private void applyTypedValue(String value) {
        this.textValue = value == null ? "" : value;

        if (syncingText) {
            return;
        }

        Identifier parsedValue = parseIdentifier(this.textValue);
        if (parsedValue != null) {
            super.setValue(parsedValue);
        }
    }

    private void commitOrRollbackText() {
        Identifier parsedValue = parseIdentifier(textValue);
        if (parsedValue == null) {
            syncTextFromValue(this.getValue());
            return;
        }

        Identifier normalized = normalizeIdentifier(parsedValue, getDefaultValue());
        super.setValue(normalized);
        syncTextFromValue(normalized);
    }

    @Override
    public void setValue(Identifier value) {
        Identifier normalized = normalizeIdentifier(value, getDefaultValue());
        super.setValue(normalized);
        syncTextFromValue(normalized);
    }

    public void repositionElements() {
        int leadingWidth = getLeadingWidth();
        textWidget.setPosition(leadingWidth, Math.max(0, (getWidgetHeight() - TEXT_HEIGHT) / 2));
        textWidget.setWidth(Math.max(20, this.getWidth() - leadingWidth));
        this.setHeight(getWidgetHeight());
    }

    @Override
    protected void renderEntry(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderLeading(guiGraphics, mouseX, mouseY, partialTick);
        textWidget.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        return ContainerEventHandler.super.mouseClicked(event, isDoubleClick) || super.mouseClicked(event, isDoubleClick);
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

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return ContainerEventHandler.super.nextFocusPath(event);
    }

    @Override
    public boolean isFocused() {
        return ContainerEventHandler.super.isFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.setFocused((GuiEventListener) null);
        }
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(textWidget);
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean isDragging) {
        dragging = isDragging;
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        if (this.focused == focused) {
            return;
        }

        if (this.focused != null) {
            this.focused.setFocused(false);
        }

        if (focused != null) {
            focused.setFocused(true);
        }

        this.focused = focused;
    }

    @Override
    public void setSearchHighlightQuery(@Nullable String query) {
        textWidget.setSearchHighlightQuery(query);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || textWidget.isMouseOver(mouseX, mouseY);
    }

    @Override
    public String getPath() {
        PathContainerWidget parent = this.getParent();
        return parent == null ? "" : parent.getPath();
    }

    @Override
    public void moveToTheTop() {
        PathContainerWidget parent = this.getParent();
        if (parent != null) {
            parent.moveToTheTop();
        }
    }

    @Override
    public void requestRelayout() {
        repositionElements();
        super.requestRelayout();
    }
}
