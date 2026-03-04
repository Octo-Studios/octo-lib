package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetRegistry;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import it.hurts.shatterbyte.shatterlib.module.config.util.Json5Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListEntryWidget<E> extends AbstractWidget implements Child<ListWidget<E>>, ContainerEventHandler, DynamicallySized, PathContainerWidget {
    private ListWidget<E> parent;
    private final int index;
    private final List<GuiEventListener> childListeners;
    private final int preferredEntryWidth;
    private String searchQuery = "";

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

        E defaultValue;
        List<E> defaultList = parent.getDefaultValue();
        if (defaultList != null && index < defaultList.size()) {
            defaultValue = defaultList.get(index);
        } else {
            defaultValue = EntryWidgetRegistry.getDefaultValue(parent.getElementClass());
        }

        entryWidget = (AbstractEntryWidget<E>) EntryWidgetRegistry
                .getFactory(parent.getElementClass())
                .create(
                        parent.getConfig(),
                        parent.getElementGenericType(),
                        parent.getAnnotations(),
                        this,
                        defaultValue,
                        () -> {
                            List<E> values = parent.getValue();
                            if (values == null || index < 0 || index >= values.size()) {
                                return defaultValue;
                            }

                            return values.get(index);
                        },
                        v -> {
                            ArrayList<E> list = new ArrayList<>();
                            List<E> values = parent.getValue();
                            if (values != null) {
                                list.addAll(values);
                            }

                            if (index < 0) {
                                return;
                            }

                            while (list.size() <= index) {
                                list.add(EntryWidgetRegistry.getDefaultValue(parent.getElementClass()));
                            }

                            list.set(index, (E) v);
                            parent.setValue(list);
                        }
                );
        preferredEntryWidth = entryWidget.getWidth();

        // buttons
        up = new IconButtonWidget<>(0, 0, 13, 14, () -> parent.moveIndex(index, index - 1), UIElements.ICON_UP);
        down = new IconButtonWidget<>(0, 0, 13, 14, () -> parent.moveIndex(index, index + 1), UIElements.ICON_DOWN);
        remove = new IconButtonWidget<>(0, 0, 13, 14, () -> parent.removeIndex(index), UIElements.ICON_MINUS);

        up.setParent(this);
        down.setParent(this);
        remove.setParent(this);
        entryWidget.setParent(this);

        childListeners = List.of(up, down, remove, entryWidget);
    }

    public void requestRelayout() {
        //this.repositionElements();

        if (parent != null) {
            parent.relayoutAndPropagate();
        }
    }

    void applySearchQuery(String query) {
        this.searchQuery = normalizeSearchQuery(query);

        if (entryWidget instanceof GenericObjectWidget objectWidget) {
            objectWidget.applySearchQuery(this.searchQuery);
        } else if (entryWidget instanceof ListWidget<?> listWidget) {
            listWidget.applySearchQuery(this.searchQuery);
        } else if (entryWidget instanceof MapWidget<?> mapWidget) {
            mapWidget.applySearchQuery(this.searchQuery);
        }
    }

    boolean hasSearchResults() {
        if (searchQuery.isEmpty()) {
            return true;
        }

        if (containsEntryValue(entryWidget, searchQuery)) {
            return true;
        }

        if (entryWidget instanceof GenericObjectWidget objectWidget) {
            return objectWidget.hasSearchResults();
        }

        if (entryWidget instanceof ListWidget<?> listWidget) {
            return listWidget.hasSearchResults();
        }

        if (entryWidget instanceof MapWidget<?> mapWidget) {
            return mapWidget.hasSearchResults();
        }

        return false;
    }

    private static boolean containsEntryValue(AbstractEntryWidget<?> widget, String normalizedQuery) {
        if (widget == null) {
            return false;
        }

        return containsSearchToken(toSearchText(widget.getValue()), normalizedQuery);
    }

    private static boolean containsSearchToken(@Nullable String value, String normalizedQuery) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return value.toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }

    private static String normalizeSearchQuery(@Nullable String query) {
        if (query == null) {
            return "";
        }

        return query.toLowerCase(Locale.ROOT).trim();
    }

    private static String toSearchText(@Nullable Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof CharSequence
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Character
                || value instanceof Enum<?>) {
            return String.valueOf(value);
        }

        try {
            return Json5Utils.encode(value).toString();
        } catch (Throwable ignored) {
            return String.valueOf(value);
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
        int contentRight = remove.getLocalX() - 4;
        int availableWidth = Math.max(20, contentRight - x);
        int stackedAvailableWidth = Math.max(20, this.getWidth() - x);

        boolean isDynamicallySized = entryWidget instanceof DynamicallySized;
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        boolean overflowsInlineArea = !isDynamicallySized && preferredEntryWidth > availableWidth;
        boolean overflowsScreen = !isDynamicallySized
                && this.getX() + x + preferredEntryWidth > screenWidth;

        if (isDynamicallySized || overflowsInlineArea || overflowsScreen) {
            int y = up.getLocalY() + up.getHeight() + 4;

            entryWidget.setPosition(x, y);
            entryWidget.setWidth(stackedAvailableWidth);

            if (entryWidget instanceof DynamicallySized dynamicallySized) {
                dynamicallySized.repositionElements();
            }

            this.setHeight(entryWidget.getLocalY() + entryWidget.getHeight() + 4);
            return;
        }

        entryWidget.setPosition(x, 2);
        entryWidget.setWidth(preferredEntryWidth);

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
        return childListeners;
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
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) {
            return true;
        }

        return (entryWidget != null && entryWidget.isMouseOver(mouseX, mouseY))
                || up.isMouseOver(mouseX, mouseY)
                || down.isMouseOver(mouseX, mouseY)
                || remove.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.setFocused(null);
        }
    }

    @Override
    public void playDownSound(SoundManager handler) {}

    @Override
    public String getPath() {
        if (this.getParent() == null || this.getParent().getParent() == null) {
            return "[" + index + "]";
        }

        return this.getParent().getParent().getPath() + "[" + index + "]";
    }
}
