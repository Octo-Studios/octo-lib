package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetRegistry;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MapWidget<V> extends AbstractEntryWidget<Map>
        implements DynamicallySized, ContainerEventHandler {

    public final List<MapEntryWidget<V>> entries = new ArrayList<>();
    public final List<MapEntryWidget<V>> renderables = new ArrayList<>();
    private final List<GuiEventListener> childListeners = new ArrayList<>();

    IconButtonWidget<MapWidget<V>> addButton =
            new IconButtonWidget<>(0, 0, 65, 14, this::addNewEntry, UIElements.ICON_PLUS);
    CollapseButtonWidget<MapWidget<V>> collapseButton = new CollapseButtonWidget<>(this::toggleCollapsed, this::isCollapsed);

    private boolean dragging;
    private GuiEventListener focused;
    @Getter
    private boolean collapsed;

    @Getter
    private final Class<V> valueClass;
    @Getter
    private final Type valueGenericType;
    @Getter
    private final Annotation[] annotations;
    private String searchQuery = "";

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
        collapseButton.setParent(this);
        rebuild();
    }

    private void rebuild() {
        entries.clear();
        renderables.clear();

        Map<String, V> map = getSafeValue();
        int i = 0;

        for (Map.Entry<String, V> e : map.entrySet()) {
            MapEntryWidget<V> entry =
                    new MapEntryWidget<>(this, e.getKey(), i++);
            entry.applySearchQuery(searchQuery);
            entries.add(entry);
            renderables.add(entry);
        }

        refreshChildListeners();

        relayoutAndPropagate();
    }

    public void addNewEntry() {
        Map<String, V> map = new LinkedHashMap<>(getSafeValue());

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
        Map<String, V> map = new LinkedHashMap<>(getSafeValue());
        map.remove(key);
        setValue(map);
        rebuild();
    }

    void moveIndex(int from, int to) {
        if (from == to) return;

        LinkedHashMap<String, V> oldMap = new LinkedHashMap<>(getSafeValue());
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

        Map<String, V> oldMap = new LinkedHashMap<>(getSafeValue());
        if (!oldMap.containsKey(oldKey) || oldMap.containsKey(newKey)) return;

        LinkedHashMap<String, V> renamed = new LinkedHashMap<>();
        for (Map.Entry<String, V> entry : oldMap.entrySet()) {
            if (entry.getKey().equals(oldKey)) {
                renamed.put(newKey, entry.getValue());
            } else {
                renamed.put(entry.getKey(), entry.getValue());
            }
        }

        setValue(renamed);

        for (MapEntryWidget<V> entry : entries) {
            if (entry.hasKey(oldKey)) {
                entry.renameKey(newKey);
                break;
            }
        }
    }

    void setValueFor(String key, V value) {
        Map<String, V> map = new LinkedHashMap<>(getSafeValue());
        map.put(key, value);
        setValue(map);
    }

    private Map<String, V> getSafeValue() {
        Map<String, V> map = getValue();
        if (map == null) {
            return Map.of();
        }

        return map;
    }

    public void setSearchQuery(@Nullable String query) {
        if (applySearchQuery(query)) {
            relayoutAndPropagate();
        }
    }

    boolean applySearchQuery(@Nullable String query) {
        String normalizedQuery = normalizeSearchQuery(query);
        if (Objects.equals(this.searchQuery, normalizedQuery)) {
            return false;
        }

        this.searchQuery = normalizedQuery;
        for (MapEntryWidget<V> entry : entries) {
            entry.applySearchQuery(normalizedQuery);
        }

        return true;
    }

    boolean hasSearchResults() {
        if (searchQuery.isEmpty()) {
            return true;
        }

        for (MapEntryWidget<V> entry : entries) {
            if (entry.hasSearchResults()) {
                return true;
            }
        }

        return false;
    }

    private static String normalizeSearchQuery(@Nullable String query) {
        if (query == null) {
            return "";
        }

        return query.toLowerCase(Locale.ROOT).trim();
    }

    @Override
    public void repositionElements() {
        collapseButton.setPosition(0, 0);

        if (collapsed) {
            this.setHeight(14);
            return;
        }

        int y = 8;

        for (MapEntryWidget<V> entry : entries) {
            entry.setPosition(4, y);
            entry.setWidth(this.width - 4);
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
            childListeners.addAll(entries);
            childListeners.add(addButton);
        }
    }

    @Override
    protected void renderEntry(GuiGraphics g, int mouseX, int mouseY, float pt) {
        //g.hLine(getX(), getX() + width - 1, getY() + 1, 0xff1c1c17);
        //g.hLine(getX(), getX() + width - 1, getY() + 2, 0xff3c3c42);
        collapseButton.render(g, mouseX, mouseY, pt);

        if (collapsed) {
            return;
        }

        collapseButton.renderExpandedBranchLine(g);
        addButton.render(g, mouseX, mouseY, pt);

        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int left = getX();
        int right = left + width - 1;

        for (int i = renderables.size() - 1; i >= 0; i--) {
            MapEntryWidget<V> e = renderables.get(i);
            int entryY = e.getY();
            int entryBottom = entryY + e.getHeight();
            if (entryBottom < 0 || entryY > screenHeight) {
                continue;
            }

//            if (i < renderables.size() - 1) {
//                g.hLine(left, right, entryBottom + 1, 0xff1c1c17);
//                g.hLine(left, right, entryBottom + 2, 0xff3c3c42);
//            }
            e.render(g, mouseX, mouseY, pt);
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

        if (addButton.isMouseOver(mouseX, mouseY)) {
            return true;
        }

        for (MapEntryWidget<V> entry : entries) {
            if (entry.isMouseOver(mouseX, mouseY)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        List<GuiEventListener> listeners = new ArrayList<>();
        listeners.add(collapseButton);

        if (!collapsed) {
            listeners.addAll(renderables);
            listeners.add(addButton);
        }

        return listeners;
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return ContainerEventHandler.super.nextFocusPath(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        boolean childHandled = ContainerEventHandler.super.mouseClicked(event, isDoubleClick);
        if (childHandled) {
            return true;
        }

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

    @Override
    public void playDownSound(SoundManager handler) {}
}
