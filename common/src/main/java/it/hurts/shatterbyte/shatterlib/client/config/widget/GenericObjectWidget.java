package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Name;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.entity.vehicle.Minecart;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericObjectWidget extends AbstractEntryWidget<Object> implements ContainerEventHandler {
    List<FieldWidget> widgets = new ArrayList<>();

    AbstractEntryWidget<?> focused;
    boolean dragging = false;

    @Getter
    public boolean collapsed = true;

    public GenericObjectWidget(ShatterConfig config, Object defaultValue, Supplier<Object> getter, Consumer<Object> setter) {
        super(defaultValue, getter, setter, 0, 0, 100, 100);
        this.populateWidget();
        this.repositionWidgets();
    }

    public void repositionWidgets() {
        int totalHeight = 8;
        int maxWidth = 0;
        Font font = Minecraft.getInstance().font;

        for (FieldWidget field : widgets) {
            field.setPosition(4 + width, totalHeight);
            maxWidth = Math.max(maxWidth, field.getLocalX() + field.getWidth() + 4);
            totalHeight += field.getHeight() + 4;
        }

        this.setWidth(maxWidth);
        this.setHeight(totalHeight);
    }

    @SneakyThrows
    private void populateWidget() {
        Object object = this.getValue();
        Class<?> clazz = object.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            FieldWidget.createFromField(config, object, field);
        }
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        guiGraphics.fill(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight(), 0x55000000);
        this.widgets.forEach((info, widget) -> {
            guiGraphics.drawString(font, info.name+": ", this.getX() + 4, widget.getY(), 0xffffffff, true);
            widget.render(guiGraphics, mouseX, mouseY, partialTick);
        });
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
        return new ArrayList<>(widgets.values());
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
        this.focused = (AbstractEntryWidget<?>) focused;
    }

    record FieldInfo(String name, String description) {}

    public String getPath() {
        if (this.getParent() != null) {
            return this.getParent().getPath();
        }

        return "";
    }
}
