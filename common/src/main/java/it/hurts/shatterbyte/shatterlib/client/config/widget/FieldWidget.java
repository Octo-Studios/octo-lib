package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Name;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FieldWidget extends AbstractWidget implements ContainerEventHandler, Child<GenericObjectWidget>, DynamicallySized {
    GenericObjectWidget parent;
    GenericObjectWidget.FieldInfo info;
    protected String fieldName = "";
    AbstractEntryWidget<?> entryWidget;
    ResetFieldButtonWidget resetButton;

    Font font = Minecraft.getInstance().font;

    boolean dragging = false;
    GuiEventListener focused;

    FieldWidget() {
        super(0, 0, 16, 16, Component.empty());
    }

    @Override
    public void repositionElements() {
        this.setWidth(this.getParent().getWidth() - 8);

        if (entryWidget instanceof DynamicallySized stuffInside) {
            stuffInside.repositionElements();
        }

        int y = 0;
        if (entryWidget.getWidth() > this.getWidth() - font.width(info.name()+": ")) {
            y = 12;
        }
        resetButton.setPosition(this.width - 4 - this.resetButton.getWidth(), y);
        entryWidget.setPosition(resetButton.getLocalX() - 4 - this.entryWidget.getWidth(), y);

        this.setHeight(Math.max(this.height, entryWidget.getHeight() + 4 + entryWidget.getLocalY()));
    }

    @SneakyThrows
    public static @Nullable FieldWidget createFromField(ShatterConfig config, String path, Object parentObject, Field field, GenericObjectWidget parent) {
        FieldWidget fieldWidget = new FieldWidget();

        Class<?> clazz = parentObject.getClass();

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

        AbstractEntryWidget<?> widget = AbstractEntryWidget.tryCreate(path, fieldWidget, config, privateLookup, field, parentObject);

        if (widget == null) {
            return null;
        }

        fieldWidget.entryWidget = widget;
        fieldWidget.resetButton = new ResetFieldButtonWidget(widget);
        fieldWidget.resetButton.setParent(fieldWidget);

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
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawString(font, info.name(), this.getX(), this.getY(), 0xffffffff, true);
        entryWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        resetButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public List<? extends GuiEventListener> children() {
        List<GuiEventListener> children = new ArrayList<>();
        children.add(entryWidget);
        children.add(resetButton);
        return children;
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
        if (this.focused != null) {
            this.focused.setFocused(false);
        }

        if (focused != null) {
            focused.setFocused(true);
        }

        this.focused = focused;
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return ContainerEventHandler.super.nextFocusPath(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        super.mouseClicked(event, isDoubleClick);
        return ContainerEventHandler.super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        super.mouseReleased(event);
        return ContainerEventHandler.super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        super.mouseDragged(event, mouseX, mouseY);
        return ContainerEventHandler.super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean isFocused() {
        return ContainerEventHandler.super.isFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        ContainerEventHandler.super.setFocused(focused);
    }

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
}
