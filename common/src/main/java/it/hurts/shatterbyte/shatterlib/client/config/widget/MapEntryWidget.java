package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetRegistry;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
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
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public class MapEntryWidget<V> extends AbstractWidget
        implements Child<MapWidget<V>>, ContainerEventHandler, DynamicallySized, PathContainerWidget {
    private static final int MIN_KEY_WIDTH = 40;
    private static final int KEY_TEXT_HORIZONTAL_PADDING = 12;

    private MapWidget<V> parent;
    private String key;
    private final int index;
    private final List<GuiEventListener> childListeners;
    private final int preferredEntryWidth;

    private boolean dragging;
    private GuiEventListener focused;

    TextAreaWidget keyWidget;
    AbstractEntryWidget<V> entryWidget;

    IconButtonWidget<MapEntryWidget<V>> up;
    IconButtonWidget<MapEntryWidget<V>> down;
    IconButtonWidget<MapEntryWidget<V>> remove;

    @SuppressWarnings("unchecked")
    public MapEntryWidget(MapWidget<V> parent, String key, int index) {
        super(0, 0, 200, 16, Component.empty());
        this.parent = parent;
        this.key = key;
        this.index = index;

        V defaultEntryValue = resolveDefaultEntryValue(parent, key);

        keyWidget = new TextAreaWidget(
                parent.getConfig(),
                String.class,
                new Annotation[]{},
                this,
                key,
                () -> this.key,
                newKey -> {
                    parent.renameKey(this.key, newKey);
                    parent.relayoutAndPropagate();
                }
        );
        keyWidget.setPredicate(s -> !s.isBlank()
                && !s.contains(" ")
                && !s.contains("'")
                && !s.contains("\"")
                && !s.contains("[")
                && !s.contains("]"));

        entryWidget = (AbstractEntryWidget<V>) EntryWidgetRegistry
                .getFactory(parent.getValueClass())
                .create(
                        parent.getConfig(),
                        parent.getValueGenericType(),
                        parent.getAnnotations(),
                        this,
                        defaultEntryValue,
                        () -> {
                            Map<String, V> map = parent.getValue();
                            if (map == null) {
                                return defaultEntryValue;
                            }

                            return map.getOrDefault(this.key, defaultEntryValue);
                        },
                        v -> parent.setValueFor(this.key, (V) v)
                );
        preferredEntryWidth = entryWidget.getWidth();

        up = new IconButtonWidget<>(0, 0, 13, 14, () -> parent.moveIndex(index, index - 1), UIElements.ICON_UP);
        down = new IconButtonWidget<>(0, 0, 13, 14, () -> parent.moveIndex(index, index + 1), UIElements.ICON_DOWN);
        remove = new IconButtonWidget<>(0, 0, 13, 14,
                () -> parent.removeKey(this.key),
                UIElements.ICON_MINUS
        );

        up.setParent(this);
        down.setParent(this);
        keyWidget.setParent(this);
        entryWidget.setParent(this);
        remove.setParent(this);

        childListeners = List.of(up, down, remove, keyWidget, entryWidget);
    }

    @SuppressWarnings("unchecked")
    private V resolveDefaultEntryValue(MapWidget<V> parent, String key) {
        Map<String, V> defaultMap = parent.getDefaultValue();
        if (defaultMap != null && defaultMap.containsKey(key)) {
            return defaultMap.get(key);
        }

        return (V) EntryWidgetRegistry.getDefaultValue(parent.getValueClass());
    }

    public void requestRelayout() {
        //this.repositionElements();

        if (parent != null) {
            parent.relayoutAndPropagate();
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

        int leftLimit = x;
        int contentRight = remove.getLocalX() - 4;
        int availableWidth = Math.max(20, contentRight - leftLimit);
        int stackedEntryWidth = Math.max(20, this.getWidth() - leftLimit);

        boolean moveDown = false;

        boolean isDynamicallySized = entryWidget instanceof DynamicallySized;
        int requiredInlineEntryWidth = isDynamicallySized ? 0 : Math.max(MIN_KEY_WIDTH, preferredEntryWidth);
        int desiredKeyWidth = getDesiredKeyWidth();
        int maxInlineKeyWidth = contentRight - x - requiredInlineEntryWidth;
        if (maxInlineKeyWidth < MIN_KEY_WIDTH) {
            moveDown = true;
        }

        if (!moveDown) {
            int keyWidth = clamp(desiredKeyWidth, MIN_KEY_WIDTH, maxInlineKeyWidth);
            keyWidget.setPosition(leftLimit, 2);
            keyWidget.setWidth(keyWidth);

            int inlineEntryX = leftLimit + keyWidth + 4;
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            boolean overflowsScreen = !isDynamicallySized
                    && this.getX() + inlineEntryX + preferredEntryWidth > screenWidth;

            if (isDynamicallySized || overflowsScreen) {
                int y = keyWidget.getLocalY() + keyWidget.getHeight() + 4;

                entryWidget.setPosition(leftLimit, y);
                entryWidget.setWidth(stackedEntryWidth);

                if (entryWidget instanceof DynamicallySized ds) {
                    ds.repositionElements();
                }

                this.setHeight(
                        entryWidget.getLocalY() + entryWidget.getHeight() + 4
                );
                return;
            }

            x += keyWidth + 4;
            entryWidget.setPosition(x, 2);
            entryWidget.setWidth(preferredEntryWidth);

            this.setHeight(Math.max(
                    16,
                    Math.max(keyWidget.getHeight(), entryWidget.getHeight()) + 4
            ));
        } else {
            int stackedMinWidth = Math.min(MIN_KEY_WIDTH, availableWidth);
            int stackedKeyWidth = clamp(desiredKeyWidth, stackedMinWidth, availableWidth);
            keyWidget.setPosition(leftLimit, 2);
            keyWidget.setWidth(stackedKeyWidth);

            int y = keyWidget.getLocalY() + keyWidget.getHeight() + 4;

            entryWidget.setPosition(leftLimit, y);
            entryWidget.setWidth(stackedEntryWidth);

            if (entryWidget instanceof DynamicallySized ds) {
                ds.repositionElements();
            }

            this.setHeight(
                    entryWidget.getLocalY() + entryWidget.getHeight() + 4
            );
        }
    }

    private int getDesiredKeyWidth() {
        String value = this.key == null ? "" : this.key;
        int contentWidth = Minecraft.getInstance().font.width(value);
        return Math.max(MIN_KEY_WIDTH, contentWidth + KEY_TEXT_HORIZONTAL_PADDING);
    }

    private static int clamp(int value, int min, int max) {
        if (max < min) {
            return max;
        }

        return Math.max(min, Math.min(value, max));
    }

    @Override public MapWidget<V> getParent() { return parent; }

    boolean hasKey(String key) {
        return this.key.equals(key);
    }

    void renameKey(String newKey) {
        this.key = newKey;
    }

    @Override
    public void setParent(@Nullable MapWidget<V> parent) {
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
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
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

        up.render(guiGraphics, mouseX, mouseY, partialTick);
        down.render(guiGraphics, mouseX, mouseY, partialTick);
        remove.render(guiGraphics, mouseX, mouseY, partialTick);
        keyWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        entryWidget.render(guiGraphics, mouseX, mouseY, partialTick);
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
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) {
            return true;
        }

        return (keyWidget != null && keyWidget.isMouseOver(mouseX, mouseY))
                || (entryWidget != null && entryWidget.isMouseOver(mouseX, mouseY))
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
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public String getPath() {
        if (parent == null || parent.getParent() == null) {
            return "['" + key + "']";
        }

        return parent.getParent().getPath() + "['" + key + "']";
    }
}
