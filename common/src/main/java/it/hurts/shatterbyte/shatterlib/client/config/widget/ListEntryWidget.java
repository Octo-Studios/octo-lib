package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetRegistry;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ListEntryWidget<E> extends AbstractWidget implements Child<ListWidget<E>>, ContainerEventHandler, DynamicallySized, PathContainerWidget {
    private ListWidget<E> parent;
    private final int index;

    AbstractEntryWidget<E> entryWidget;

    IconButtonWidget<ListEntryWidget<E>> up;
    IconButtonWidget<ListEntryWidget<E>> down;
    IconButtonWidget<ListEntryWidget<E>> remove;

    private boolean dragging;
    private GuiEventListener focused;

    @SuppressWarnings("unchecked")
    public ListEntryWidget(ListWidget<E> parent, int index) {
        super(0, 0, 200, 16, Component.empty());
        this.parent = parent;
        this.index = index;

        // create entry widget via factory using indexed path
        String path = parent.getParent().getPath() + "[" + index + "]";
        E defaultValue;
        if (index < parent.getDefaultValue().size()) {
            defaultValue = (E) parent.getDefaultValue().get(index);
        } else {
            defaultValue = EntryWidgetRegistry.getDefaultValue(parent.getElementClass());
        }

        entryWidget = (AbstractEntryWidget<E>) EntryWidgetRegistry
                .getFactory(parent.getElementClass())
                .create(
                        parent.getConfig(),
                        parent.getElementGenericType(),
                        parent.getAnnotations(),
                        parent.getParent(),
                        defaultValue,
                        () -> parent.getValue().get(index),
                        v -> {
                            ArrayList<E> list = new ArrayList<>(parent.getValue());
                            list.set(index, (E) v);
                            parent.setValue(list);
                        }
                );

        // buttons
        up = new IconButtonWidget<>(0, 0, 13, 14, () -> parent.moveIndex(index, index - 1), UIElements.ICON_UP);
        down = new IconButtonWidget<>(0, 0, 13, 14, () -> parent.moveIndex(index, index + 1), UIElements.ICON_DOWN);
        remove = new IconButtonWidget<>(0, 0, 13, 14, () -> parent.removeIndex(index), UIElements.ICON_MINUS);

        up.setParent(this);
        down.setParent(this);
        remove.setParent(this);
        entryWidget.setParent(this);
    }

    public void requestRelayout() {
        //this.repositionElements();

        if (parent != null) {
            parent.repositionElements();
        }
    }

    @Override
    public void repositionElements() {
        int x = 4;

        up.setPosition(x, 2);
        x += up.getWidth() + 2;

        down.setPosition(x, 2);
        x += down.getWidth() + 4;

        remove.setPosition(this.getWidth() - remove.getWidth() - 4, 2);

        entryWidget.setPosition(x, 2);
        if (entryWidget instanceof DynamicallySized dynamicallySized) {
            entryWidget.setWidth(this.width - x - 4 - (remove.getWidth() + 4));
            dynamicallySized.repositionElements();
        }

        if (entryWidget instanceof DynamicallySized ds) {
            ds.repositionElements();
        }

        this.setHeight(Math.max(16, entryWidget.getHeight() + 4));
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float pt) {
        //g.fill(getX(), getY(), getX() + width, getY() + height, 0x18000000);
        if (this.index == 0) {
            up.active = false;
        } else {
            up.active = true;
        }

        if ((this.index + 1) >= this.parent.entries.size()) {
            down.active = false;
        } else {
            down.active = true;
        }

        up.render(g, mouseX, mouseY, pt);
        down.render(g, mouseX, mouseY, pt);
        remove.render(g, mouseX, mouseY, pt);
        entryWidget.render(g, mouseX, mouseY, pt);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override public ListWidget<E> getParent() { return parent; }

    @Override
    public void setParent(@Nullable ListWidget<E> parent) {
        this.parent = parent;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(up, down, remove, entryWidget);
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
        return super.isMouseOver(mouseX, mouseY) || this.children().stream().anyMatch(child -> child.isMouseOver(mouseX, mouseY));
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
        return this.getParent().getParent().getPath()+"["+index+"]";
    }
}
