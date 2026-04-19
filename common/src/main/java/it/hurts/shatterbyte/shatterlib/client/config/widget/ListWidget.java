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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ListWidget<E> extends AbstractEntryWidget<List>
        implements DynamicallySized, ContainerEventHandler {

    public final List<ListEntryWidget<E>> entries = new ArrayList<>();
    public final List<ListEntryWidget<E>> renderables = new ArrayList<>();
    private final List<GuiEventListener> childListeners = new ArrayList<>();
    IconButtonWidget<ListWidget<E>> addButton = new IconButtonWidget<>(0, 0, 65, 14, this::addNewEntry, UIElements.ICON_PLUS);
    CollapseButtonWidget<ListWidget<E>> collapseButton = new CollapseButtonWidget<>(this::toggleCollapsed, this::isCollapsed);

    private boolean dragging;
    private GuiEventListener focused;
    @Getter
    private boolean collapsed;

    @Getter
    private Annotation[] annotations;

    @Getter
    private final Class<E> elementClass;

    @Getter
    private final Type elementGenericType;
    private String searchQuery = "";

    public ListWidget(
            ShatterConfig config,
            Type type,
            Annotation[] annotations,
            PathContainerWidget parent,
            List defaultValue,
            Supplier<List> getter,
            Consumer<List> setter
    ) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 100, 100);

        if (!(type instanceof ParameterizedType pt)) {
            throw new RuntimeException("List field without generic type");
        }

        Type arg = pt.getActualTypeArguments()[0];

        Class<?> rawElementClass;
        if (arg instanceof Class<?> c) {
            rawElementClass = c;
        } else if (arg instanceof ParameterizedType p) {
            rawElementClass = (Class<?>) p.getRawType();
        } else {
            throw new RuntimeException("Unsupported list element type: " + arg);
        }

        @SuppressWarnings("unchecked")
        Class<Object> elementClass = (Class<Object>) rawElementClass;

        addButton.setParent(this);
        collapseButton.setParent(this);
        this.elementClass = (Class<E>) elementClass;
        this.elementGenericType = arg;

        this.annotations = annotations;
        rebuild();
    }

    /* ---------- core ---------- */

    private void rebuild() {
        entries.clear();
        renderables.clear();

        List<E> list = getSafeValue();
        for (int i = 0; i < list.size(); i++) {
            ListEntryWidget<E> entry = new ListEntryWidget<>(this, i);
            entry.applySearchQuery(searchQuery);
            entries.add(entry);
            renderables.add(entry);
        }

        refreshChildListeners();

        relayoutAndPropagate();
    }

    void removeIndex(int index) {
        ArrayList<E> list = new ArrayList<>(getSafeValue());
        if (index < 0 || index >= list.size()) {
            return;
        }

        list.remove(index);
        setValue(list);
        rebuild();
    }

    void moveIndex(int from, int to) {
        List<E> current = getSafeValue();
        if (from < 0 || from >= current.size()) return;
        if (to < 0 || to >= current.size()) return;

        ArrayList<E> list = new ArrayList<>(current);
        E v = list.remove(from);
        list.add(to, v);
        setValue(list);
        rebuild();
    }

    public void addNewEntry() {
        ArrayList<E> list = new ArrayList<>(getSafeValue());
        list.add(EntryWidgetRegistry.getDefaultValue(elementClass));
        setValue(list);
        rebuild();
    }

    private List<E> getSafeValue() {
        List<E> list = getValue();
        if (list == null) {
            return List.of();
        }

        return list;
    }

    public void setSearchQuery(@Nullable String query) {
        if (applySearchQuery(query)) {
            relayoutAndPropagate();
        }
    }

    void applySearchHighlight(String query) {
        String normalizedQuery = normalizeSearchQuery(query);
        for (ListEntryWidget<E> entry : entries) {
            entry.applySearchHighlight(normalizedQuery);
        }
    }

    boolean applySearchQuery(@Nullable String query) {
        String normalizedQuery = normalizeSearchQuery(query);
        if (Objects.equals(this.searchQuery, normalizedQuery)) {
            return false;
        }

        this.searchQuery = normalizedQuery;
        for (ListEntryWidget<E> entry : entries) {
            entry.applySearchQuery(normalizedQuery);
        }

        return true;
    }

    boolean hasSearchResults() {
        if (searchQuery.isEmpty()) {
            return true;
        }

        for (ListEntryWidget<E> entry : entries) {
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

    void collectSearchMatches(String normalizedQuery, List<FieldWidget> matches) {
        if (normalizedQuery.isEmpty()) {
            return;
        }

        for (ListEntryWidget<E> entry : entries) {
            entry.collectSearchMatches(normalizedQuery, matches);
        }
    }

    /* ---------- layout ---------- */

    @Override
    public void repositionElements() {
        collapseButton.setPosition(0, 0);

        if (collapsed) {
            this.setHeight(14);
            return;
        }

        int y = 8;

        for (ListEntryWidget<E> entry : entries) {
            entry.setPosition(4, y);
            entry.setWidth(this.width - 4);
            entry.repositionElements();

            y += entry.getHeight() + 4;
        }

        addButton.setPosition(this.getWidth()/2-addButton.getWidth()/2, y);
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

    /* ---------- render ---------- */

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float pt) {
        //guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0x22000000);
        //guiGraphics.hLine(this.getX(), this.getX() + this.width -1, this.getY() + 1, 0xff1c1c17);
        //guiGraphics.hLine(this.getX(), this.getX() + this.width -1, this.getY() + 2, 0xff3c3c42);
        collapseButton.render(guiGraphics, mouseX, mouseY, pt);

        if (collapsed) {
            return;
        }

        collapseButton.renderExpandedBranchLine(guiGraphics);
        addButton.render(guiGraphics, mouseX, mouseY, pt);

        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int left = this.getX();
        int right = left + this.width - 1;

        for (int i = renderables.size() - 1; i >= 0; i--) {
            ListEntryWidget<E> widget = renderables.get(i);
            int widgetY = widget.getY();
            int widgetBottom = widgetY + widget.getHeight();
            if (widgetBottom < 0 || widgetY > screenHeight) {
                continue;
            }

//            if (i < renderables.size() - 1) {
//                guiGraphics.hLine(left, right, widgetBottom + 1, 0xff1c1c17);
//                guiGraphics.hLine(left, right, widgetBottom + 2, 0xff3c3c42);
//            }
            widget.render(guiGraphics, mouseX, mouseY, pt);
        }
    }

    /* ---------- container ---------- */

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

        for (ListEntryWidget<E> entry : entries) {
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
            listeners.addAll(entries);
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

        if (this.isMouseOver(event.x(), event.y())) {
            this.setFocused(null);
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
    public void resetValue() {
        //this.entries.forEach(entry -> entry.entryWidget.resetValue());
        super.resetValue();
        this.rebuild();
    }

    @Override
    public void playDownSound(SoundManager handler) {}
}
