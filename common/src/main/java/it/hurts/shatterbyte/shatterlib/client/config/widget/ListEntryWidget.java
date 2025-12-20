package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetRegistry;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
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
        E value = parent.getValue().get(index);

        entryWidget = (AbstractEntryWidget<E>) EntryWidgetRegistry
                .getFactory(parent.getElementClass())
                .create(
                        parent.getConfig(),
                        parent.getParent(),
                        value,
                        () -> parent.getValue().get(index),
                        v -> {
                            ArrayList<E> list = new ArrayList<>(parent.getValue());
                            list.set(index, (E) v);
                            parent.setValue(list);
                        }
                );

        // buttons
        up = new IconButtonWidget<>(0, 0, 14, 14, () -> parent.moveIndex(index, index - 1), UIElements.ICON_UP);
        down = new IconButtonWidget<>(0, 0, 14, 14, () -> parent.moveIndex(index, index + 1), UIElements.ICON_DOWN);
        remove = new IconButtonWidget<>(0, 0, 14, 14, () -> parent.removeIndex(index), UIElements.ICON_MINUS);

        up.setParent(this);
        down.setParent(this);
        remove.setParent(this);
        entryWidget.setParent(this);
    }

    /* ---------- layout ---------- */

    @Override
    public void repositionElements() {
        int x = 4;

        up.setPosition(x, 2);
        x += up.getWidth() + 2;

        down.setPosition(x, 2);
        x += down.getWidth() + 2;

        remove.setPosition(x, 2);
        x += remove.getWidth() + 6;

        entryWidget.setPosition(x, 2);
        entryWidget.setWidth(this.width - x - 4);

        if (entryWidget instanceof DynamicallySized ds) {
            ds.repositionElements();
        }

        this.setHeight(Math.max(16, entryWidget.getHeight() + 4));
    }

    /* ---------- render ---------- */

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float pt) {
        g.fill(getX(), getY(), getX() + width, getY() + height, 0x18000000);

        up.render(g, mouseX, mouseY, pt);
        down.render(g, mouseX, mouseY, pt);
        remove.render(g, mouseX, mouseY, pt);
        entryWidget.render(g, mouseX, mouseY, pt);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    /* ---------- container ---------- */

    @Override public ListWidget<E> getParent() { return parent; }

    @Override
    public void setParent(@Nullable ListWidget<E> parent) {
        this.parent = parent;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(up, down, remove, entryWidget);
    }

    @Override public boolean isDragging() { return dragging; }
    @Override public void setDragging(boolean d) { dragging = d; }

    @Override public @Nullable GuiEventListener getFocused() { return focused; }
    @Override public void setFocused(@Nullable GuiEventListener f) { focused = f; }

    @Override
    public String getPath() {
        return this.getParent().getParent().getPath()+"["+index+"]";
    }

    @Override
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
}
