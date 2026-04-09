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
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MapWidget<V> extends AbstractEntryWidget<Map>
        implements DynamicallySized, ContainerEventHandler {

    public final List<MapEntryWidget<V>> entries = new ArrayList<>();
    public final List<MapEntryWidget<V>> renderables = new ArrayList<>();

    IconButtonWidget<MapWidget<V>> addButton =
            new IconButtonWidget<>(0, 0, 65, 14, this::addNewEntry, UIElements.ICON_PLUS);

    private boolean dragging;
    private GuiEventListener focused;

    @Getter
    private final Class<V> valueClass;
    @Getter
    private final Type valueGenericType;
    @Getter
    private final Annotation[] annotations;

    public MapWidget(
            ShatterConfig config,
            Type type,
            Annotation[] annotations,
            PathContainerWidget parent,
            Map defaultValue,
            Supplier<Map> getter,
            Consumer<Map> setter
    ) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 100, 100);

        if (!(type instanceof ParameterizedType pt)) {
            throw new RuntimeException("Map field without generic type");
        }

        // Map<String, V>
        Type valueArg = pt.getActualTypeArguments()[1];

        Class<?> raw;
        if (valueArg instanceof Class<?> c) {
            raw = c;
        } else if (valueArg instanceof ParameterizedType p) {
            raw = (Class<?>) p.getRawType();
        } else {
            throw new RuntimeException("unsupported map value type: " + valueArg);
        }

        this.valueClass = (Class<V>) raw;
        this.valueGenericType = valueArg;
        this.annotations = annotations;

        addButton.setParent(this);
        rebuild();
    }

    private void rebuild() {
        entries.clear();
        renderables.clear();

        Map<String, V> map = getValue();
        int i = 0;

        for (Map.Entry<String, V> e : map.entrySet()) {
            MapEntryWidget<V> entry =
                    new MapEntryWidget<>(this, e.getKey(), i++);
            entries.add(entry);
            renderables.add(entry);
        }

        relayoutAndPropagate();
    }

    public void addNewEntry() {
        Map<String, V> map = new LinkedHashMap<>(getValue());

        String base = "key";
        String key = base;
        int i = 1;
        while (map.containsKey(key)) {
            key = base + i++;
        }

        map.put(key, EntryWidgetRegistry.getDefaultValue(valueClass));
        setValue(map);
        rebuild();
    }

    void removeKey(String key) {
        Map<String, V> map = new LinkedHashMap<>(getValue());
        map.remove(key);
        setValue(map);
        rebuild();
    }

    void moveIndex(int from, int to) {
        if (from == to) return;

        LinkedHashMap<String, V> oldMap = new LinkedHashMap<>(getValue());
        if (from < 0 || from >= oldMap.size()) return;
        if (to < 0 || to >= oldMap.size()) return;

        List<Map.Entry<String, V>> entries = new ArrayList<>(oldMap.entrySet());

        Map.Entry<String, V> moved = entries.remove(from);
        entries.add(to, moved);

        LinkedHashMap<String, V> newMap = new LinkedHashMap<>();
        for (Map.Entry<String, V> e : entries) {
            newMap.put(e.getKey(), e.getValue());
        }

        setValue(newMap);
        rebuild();
    }

    void renameKey(String oldKey, String newKey) {
        if (oldKey.equals(newKey)) return;

        Map<String, V> map = new LinkedHashMap<>(getValue());
        if (map.containsKey(newKey)) return;

        V value = map.remove(oldKey);
        map.put(newKey, value);

        setValue(map);
        rebuild();
    }

    void setValueFor(String key, V value) {
        Map<String, V> map = new LinkedHashMap<>(getValue());
        map.put(key, value);
        setValue(map);
    }

    @Override
    public void repositionElements() {
        int y = 4;

        for (MapEntryWidget<V> entry : entries) {
            entry.setPosition(4, y);
            entry.setWidth(this.width - 8);
            entry.repositionElements();
            y += entry.getHeight() + 4;
        }

        addButton.setPosition(this.width / 2 - addButton.getWidth() / 2, y);
        y += addButton.getHeight() + 4;

        this.setHeight(Math.max(14, y));
    }

    void relayoutAndPropagate() {
        repositionElements();
        requestRelayout();
    }

    @Override
    protected void renderEntry(GuiGraphics g, int mouseX, int mouseY, float pt) {
        g.hLine(getX(), getX() + width - 1, getY() + 1, 0xff1c1c17);
        g.hLine(getX(), getX() + width - 1, getY() + 2, 0xff3c3c42);

        addButton.render(g, mouseX, mouseY, pt);

        for (MapEntryWidget<V> e : renderables.reversed()) {
            g.hLine(getX(), getX() + width - 1, e.getY() + e.getHeight() + 1, 0xff1c1c17);
            g.hLine(getX(), getX() + width - 1, e.getY() + e.getHeight() + 2, 0xff3c3c42);
            e.render(g, mouseX, mouseY, pt);
        }
    }

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

    @Override public boolean isDragging() { return dragging; }
    @Override public void setDragging(boolean d) { dragging = d; }

    @Override public @Nullable GuiEventListener getFocused() { return focused; }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        if (this.focused != focused) {
            if (this.focused != null) {
                this.focused.setFocused(false);
            }

            if (focused != null) {
                focused.setFocused(true);
            }

            this.focused = focused;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void resetValue() {
        super.resetValue();
        this.rebuild();
    }
}
