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
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;

public class MapEntryWidget<V> extends AbstractWidget
        implements Child<MapWidget<V>>, ContainerEventHandler, DynamicallySized, PathContainerWidget {

    private MapWidget<V> parent;
    private String key;
    private final int index;
    private final List<GuiEventListener> childListeners;

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

        keyWidget = new TextAreaWidget(
                parent.getConfig(),
                String.class,
                new Annotation[]{},
                parent.getParent(),
                key,
                () -> this.key,
                newKey -> parent.renameKey(this.key, newKey)
        );
        keyWidget.setPredicate(s -> !s.isBlank() && !s.contains(" "));

        entryWidget = (AbstractEntryWidget<V>) EntryWidgetRegistry
                .getFactory(parent.getValueClass())
                .create(
                        parent.getConfig(),
                        parent.getValueGenericType(),
                        parent.getAnnotations(),
                        this,
                        parent.getValue().get(key),
                        () -> parent.getValue().get(this.key),
                        v -> parent.setValueFor(this.key, (V) v)
                );

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

        int rightLimit = remove.getX() - 4;

        boolean moveDown = false;

        int keyWidth = Math.min(80, rightLimit - x - 40);
        if (keyWidth < 40) {
            moveDown = true;
        }

        if (!moveDown) {
            keyWidget.setPosition(x, 2);
            keyWidget.setWidth(keyWidth);

            x += keyWidth + 4;

            entryWidget.setPosition(x, 2);

            if (entryWidget instanceof DynamicallySized ds) {
                entryWidget.setWidth(this.width - x - remove.getWidth() - 8);
                ds.repositionElements();
            }

            this.setHeight(Math.max(
                    16,
                    Math.max(keyWidget.getHeight(), entryWidget.getHeight()) + 4
            ));
        } else {
            keyWidget.setPosition(x, 2);
            keyWidget.setWidth(rightLimit - x);

            int y = keyWidget.getY() + keyWidget.getHeight() + 4;

            entryWidget.setPosition(x, y);

            if (entryWidget instanceof DynamicallySized ds) {
                entryWidget.setWidth(rightLimit - x);
                ds.repositionElements();
            }

            this.setHeight(
                    entryWidget.getY() + entryWidget.getHeight() + 4
            );
        }
    }

    @Override public MapWidget<V> getParent() { return parent; }

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
        return super.isMouseOver(mouseX, mouseY);
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
        return parent.getParent().getPath()+"['"+key+"']";
    }
}
