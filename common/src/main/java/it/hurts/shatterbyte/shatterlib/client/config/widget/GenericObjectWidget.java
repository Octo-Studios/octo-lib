package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericObjectWidget extends AbstractEntryWidget<Object> implements ContainerEventHandler, HasStuffInside {
    List<FieldWidget> widgets = new ArrayList<>();

    FieldWidget focused;
    boolean dragging = false;

    @Getter
    public boolean collapsed = true;

    public GenericObjectWidget(ShatterConfig config, FieldWidget parent, Object defaultValue, Supplier<Object> getter, Consumer<Object> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 100, 100);
        this.populateWidget();
        this.repositionElements();
    }

    @Override
    public void repositionElements() {
        int totalHeight = 8;
        if (this.getParent() != null) {
            this.setWidth(this.getParent().getWidth() - 10 - 20);
        }

        for (FieldWidget field : widgets) {
            field.repositionElements();
            field.setPosition(6, totalHeight);

            totalHeight += field.getHeight() + 4;
        }

        this.setHeight(totalHeight);
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
        }
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //this.repositionWidgets();
        guiGraphics.fill(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight(), 0x55000000);
        this.widgets.forEach(widget -> widget.render(guiGraphics, mouseX, mouseY, partialTick));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (ContainerEventHandler.super.mouseClicked(event, isDoubleClick)) {
            return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return widgets;
    }

    @Override
    public boolean isDragging() {
        return this.dragging;
    }

    @Override
    public void setDragging(boolean isDragging) {
        this.dragging = isDragging;
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        this.focused = (FieldWidget) focused;
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

    record FieldInfo(String name, String description) {}
}
