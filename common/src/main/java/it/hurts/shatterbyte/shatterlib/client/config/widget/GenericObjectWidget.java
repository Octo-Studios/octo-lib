package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.animation.Tween;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.EaseType;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericObjectWidget extends AbstractEntryWidget<Object> implements ContainerEventHandler, DynamicallySized, Scrollable {
    List<FieldWidget> widgets = new ArrayList<>();
    List<FieldWidget> renderables = new ArrayList<>();
    private final List<GuiEventListener> childListeners = new ArrayList<>();
    CollapseButtonWidget<GenericObjectWidget> collapseButton = new CollapseButtonWidget<>(this::toggleCollapsed, this::isCollapsed);

    FieldWidget focused;
    boolean dragging = false;

    @Getter
    private boolean collapsed;

    private double scrollOffset;

    public GenericObjectWidget(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, Object defaultValue, Supplier<Object> getter, Consumer<Object> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 100, 100);
        this.collapseButton.setParent(this);

        if (this.getValue() == null && this.getDefaultValue() != null) {
            this.setValue(this.getDefaultValue());
        }

        this.populateWidget();
        this.refreshChildListeners();
        this.repositionElements();
    }

    @Override
    public int getY() {
        return super.getY() + (int) getScrollOffset();
    }

    @Override
    public void repositionElements() {
        collapseButton.setPosition(0, 0);

        if (collapsed) {
            this.setHeight(14);
            return;
        }

        int totalHeight = 8;
//        if (this.getParent() != null) {
//            this.setWidth(this.getParent().getWidth() - 10 - 14);
//        }

        for (FieldWidget field : widgets) {
            field.repositionElements();
            field.setPosition(4, totalHeight);

            totalHeight += field.getHeight() + 4;
        }

        this.setHeight(totalHeight);
    }

    void relayoutAndPropagate() {
        repositionElements();
        requestRelayout();
    }

    private void toggleCollapsed() {
        setCollapsed(!collapsed);
    }

    public void setCollapsed(boolean collapsed) {
        if (this.collapsed == collapsed) {
            return;
        }

        this.collapsed = collapsed;
        if (collapsed) {
            this.setFocused(null);
        }

        refreshChildListeners();
        relayoutAndPropagate();
    }

    private void refreshChildListeners() {
        childListeners.clear();
        childListeners.add(collapseButton);

        if (!collapsed) {
            childListeners.addAll(widgets);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) {
            return true;
        }

        if (collapseButton.isMouseOver(mouseX, mouseY)) {
            return true;
        }

        if (collapsed) {
            return false;
        }

        for (FieldWidget widget : widgets) {
            if (widget.isMouseOver(mouseX, mouseY)) {
                return true;
            }
        }

        return false;
    }

    @SneakyThrows
    private void populateWidget() {
        Object object = this.getValue();
        if (object == null) {
            return;
        }

        Class<?> clazz = object.getClass();
        String path = this.getFieldPath();

        for (Field field : getAllInstanceFields(clazz)) {
            FieldWidget fieldWidget = FieldWidget.createFromField(this.getConfig(), path, object, field, this);
            if (fieldWidget == null) {
                continue;
            }

            this.widgets.add(fieldWidget);
            this.renderables.add(fieldWidget);
        }
    }

    private static List<Field> getAllInstanceFields(Class<?> clazz) {
        List<Class<?>> hierarchy = new ArrayList<>();
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            hierarchy.add(0, current);
            current = current.getSuperclass();
        }

        List<Field> fields = new ArrayList<>();
        for (Class<?> type : hierarchy) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
        }

        return fields;
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //guiGraphics.fill(this.getX(), this.getY(), this.getX()+this.width, this.getY()+this.height, 0x20000000);
        //this.repositionWidgets();
        collapseButton.render(guiGraphics, mouseX, mouseY, partialTick);

        if (collapsed) {
            return;
        }

        collapseButton.renderExpandedBranchLine(guiGraphics);

        //guiGraphics.hLine(this.getX(), this.getX() + this.width -1, this.getY() + 1, 0xff1c1c17);
        //guiGraphics.hLine(this.getX(), this.getX() + this.width -1, this.getY() + 2, 0xff3c3c42);
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int left = this.getX();
        int right = left + this.width - 1;

        for (int i = renderables.size() - 1; i >= 0; i--) {
            FieldWidget widget = renderables.get(i);
            int widgetY = widget.getY();
            int widgetBottom = widgetY + widget.getHeight();
            if (widgetBottom < 0 || widgetY > screenHeight) {
                continue;
            }

            if (i < renderables.size() - 1) {
                guiGraphics.hLine(left + 4, right, widgetBottom + 1, 0xff1c1c17);
                guiGraphics.hLine(left + 4, right, widgetBottom + 2, 0xff3c3c42);
            }
            widget.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        //guiGraphics.vLine(this.getX()+4, this.getY()+16, this.getY()+this.getHeight(), 0xff1c1c17);
        //RenderUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.width, this.height, 0xff1f1e23);
        //guiGraphics.hLine(this.getX(), this.getX() + this.width -1, this.getY() + this.height, 0xff3c3c42);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return ContainerEventHandler.super.nextFocusPath(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        ContainerEventHandler.super.mouseClicked(event, isDoubleClick);
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
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.setFocused(null);
        }
    }

    @Override
    public List<? extends GuiEventListener> children() {
        List<GuiEventListener> listeners = new ArrayList<>();
        listeners.add(collapseButton);

        if (!collapsed) {
            listeners.addAll(renderables);
        }

        return listeners;
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
    public FieldWidget getFocused() {
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

            this.focused = (FieldWidget) focused;
        }
    }

    public String getFieldPath() {
        if (this.getParent() == null) {
            return "";
        }

        return this.getParent().getPath();
    }

    @Override
    public void resetValue() {
        this.widgets.forEach(fieldWidget -> fieldWidget.entryWidget.resetValue());
    }

    @Override
    public void playDownSound(SoundManager handler) {}

    @Override
    public double getScrollOffset() {
        return scrollOffset;
    }

    @Override
    public void setScrollOffset(double offset) {
        this.scrollOffset = offset;
    }

    record FieldInfo(String name, String description) {}
}
