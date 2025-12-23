package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetRegistry;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import lombok.Getter;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ListWidget<E> extends AbstractEntryWidget<ArrayList<E>>
        implements DynamicallySized, ContainerEventHandler {

    public final List<ListEntryWidget<E>> entries = new ArrayList<>();
    public final List<ListEntryWidget<E>> renderables = new ArrayList<>();
    IconButtonWidget<ListWidget<E>> addButton = new IconButtonWidget<>(0, 0, 65, 14, this::addNewEntry, UIElements.ICON_PLUS);

    private boolean dragging;
    private GuiEventListener focused;

    @Getter
    private final Class<E> elementClass;

    public ListWidget(
            ShatterConfig config,
            PathContainerWidget parent,
            ArrayList<E> defaultValue,
            Supplier<ArrayList<E>> getter,
            Consumer<ArrayList<E>> setter,
            Class<E> elementClass
    ) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 100, 100);
        addButton.setParent(this);
        this.elementClass = elementClass;
        rebuild();
    }

    /* ---------- core ---------- */

    private void rebuild() {
        entries.clear();
        renderables.clear();

        ArrayList<E> list = getValue();
        for (int i = 0; i < list.size(); i++) {
            ListEntryWidget<E> entry = new ListEntryWidget<>(this, i);
            entries.add(entry);
            renderables.add(entry);
        }

        repositionElements();
        requestRelayout();
    }

    void removeIndex(int index) {
        ArrayList<E> list = new ArrayList<>(getValue());
        list.remove(index);
        setValue(list);
        rebuild();
    }

    void moveIndex(int from, int to) {
        if (to < 0 || to >= getValue().size()) return;

        ArrayList<E> list = new ArrayList<>(getValue());
        E v = list.remove(from);
        list.add(to, v);
        setValue(list);
        rebuild();
    }

    public void addNewEntry() {
        ArrayList<E> list = new ArrayList<>(getValue());
        list.add(EntryWidgetRegistry.getDefaultValue(elementClass));
        setValue(list);
        rebuild();
    }

    /* ---------- layout ---------- */

    @Override
    public void repositionElements() {
        int y = 4;

        for (ListEntryWidget<E> entry : entries) {
            entry.setPosition(4, y);
            entry.setWidth(this.width - 8);
            entry.repositionElements();

            y += entry.getHeight() + 4;
        }

        addButton.setPosition(this.getWidth()/2-addButton.getWidth()/2, y);
        y += addButton.getHeight() + 4;

        this.setHeight(Math.max(14, y));
    }

    /* ---------- render ---------- */

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt) {
        //guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0x22000000);
        guiGraphics.hLine(this.getX(), this.getX() + this.width -1, this.getY() + 1, 0xff1c1c17);
        guiGraphics.hLine(this.getX(), this.getX() + this.width -1, this.getY() + 2, 0xff3c3c42);
        addButton.render(guiGraphics, mouseX, mouseY, pt);

        for (ListEntryWidget<E> widget : renderables.reversed()) {
            guiGraphics.hLine(this.getX(), this.getX() + this.width -1, widget.getY() + widget.getHeight() + 1, 0xff1c1c17);
            guiGraphics.hLine(this.getX(), this.getX() + this.width -1, widget.getY() +widget.getHeight() + 2, 0xff3c3c42);
            widget.render(guiGraphics, mouseX, mouseY, pt);
        }
    }

    /* ---------- container ---------- */

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || this.children().stream().anyMatch(child -> child.isMouseOver(mouseX, mouseY));
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return new ArrayList<GuiEventListener>(entries) {{add(addButton);}};
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return ContainerEventHandler.super.nextFocusPath(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (ContainerEventHandler.super.mouseClicked(event, isDoubleClick)) {
            return false;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (ContainerEventHandler.super.mouseReleased(event)) {
            return false;
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if (ContainerEventHandler.super.mouseDragged(event, mouseX, mouseY)) {
            return false;
        }
        return super.mouseDragged(event, mouseX, mouseY);
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

    @Override public boolean isDragging() { return dragging; }
    @Override public void setDragging(boolean d) { dragging = d; }

    @Override public @Nullable GuiEventListener getFocused() { return focused; }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        if (this.focused instanceof ListEntryWidget field) {
            field.setFocused(false);
            field.setFocused(null);
        }

        if (focused != null) {
            focused.setFocused(true);
        }

        this.focused = focused;
    }

    @Override
    public void resetValue() {
        this.entries.forEach(entry -> entry.entryWidget.resetValue());
        super.resetValue();
        this.rebuild();
    }
}
