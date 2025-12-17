package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ListWidget<E> extends AbstractEntryWidget<ArrayList<E>>
        implements DynamicallySized, ContainerEventHandler {

    private final List<ListEntryWidget<E>> entries = new ArrayList<>();

    private boolean dragging;
    private GuiEventListener focused;

    public ListWidget(
            ShatterConfig config,
            FieldWidget parent,
            ArrayList<E> defaultValue,
            Supplier<ArrayList<E>> getter,
            Consumer<ArrayList<E>> setter
    ) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 100, 100);
        rebuild();
    }

    /* ---------- core ---------- */

    private void rebuild() {
        entries.clear();

        ArrayList<E> list = getValue();
        for (int i = 0; i < list.size(); i++) {
            ListEntryWidget<E> entry = new ListEntryWidget<>(this, i);
            entries.add(entry);
        }

        repositionElements();
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

        this.setHeight(Math.max(14, y));
    }

    /* ---------- render ---------- */

    @Override
    protected void renderEntry(GuiGraphics g, int mouseX, int mouseY, float pt) {
        g.fill(getX(), getY(), getX() + width, getY() + height, 0x22000000);

        for (ListEntryWidget<E> entry : entries) {
            entry.render(g, mouseX, mouseY, pt);
        }
    }

    /* ---------- container ---------- */

    @Override
    public List<? extends GuiEventListener> children() {
        return entries;
    }

    @Override public boolean isDragging() { return dragging; }
    @Override public void setDragging(boolean d) { dragging = d; }

    @Override public @Nullable GuiEventListener getFocused() { return focused; }
    @Override public void setFocused(@Nullable GuiEventListener f) { focused = f; }
}
