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

    FieldWidget focused;
    boolean dragging = false;

    @Getter
    public boolean collapsed = true;

    private double scrollOffset;

    public GenericObjectWidget(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, Object defaultValue, Supplier<Object> getter, Consumer<Object> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 100, 100);
        this.populateWidget();
        this.repositionElements();
    }

    @Override
    public int getY() {
        return super.getY() + (int) getScrollOffset();
    }

    @Override
    public void repositionElements() {
        int totalHeight = 4;
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

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || this.children().stream().anyMatch(child -> child.isMouseOver(mouseX, mouseY));
    }

    @SneakyThrows
    private void populateWidget() {
        Object object = this.getValue();
        Class<?> clazz = object.getClass();
        String path = this.getFieldPath();

        for (Field field : clazz.getDeclaredFields()) {
            FieldWidget fieldWidget = FieldWidget.createFromField(this.getConfig(), path, object, field, this);
            if (fieldWidget == null) {
                continue;
            }

            this.widgets.add(fieldWidget);
            this.renderables.add(fieldWidget);
        }
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //guiGraphics.fill(this.getX(), this.getY(), this.getX()+this.width, this.getY()+this.height, 0x20000000);
        //this.repositionWidgets();
        guiGraphics.hLine(this.getX(), this.getX() + this.width -1, this.getY() + 1, 0xff1c1c17);
        guiGraphics.hLine(this.getX(), this.getX() + this.width -1, this.getY() + 2, 0xff3c3c42);
        this.renderables.reversed().forEach(widget -> {
            guiGraphics.hLine(this.getX(), this.getX() + this.width -1, widget.getY() + widget.getHeight() + 1, 0xff1c1c17);
            guiGraphics.hLine(this.getX(), this.getX() + this.width -1, widget.getY() +widget.getHeight() + 2, 0xff3c3c42);
            widget.render(guiGraphics, mouseX, mouseY, partialTick);
        });
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
        return widgets;
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
