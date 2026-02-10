package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScrollableWidget extends AbstractWidget implements ContainerEventHandler, PathContainerWidget {
    List<AbstractWidget> widgets = new ArrayList<>();
    private @Nullable AbstractWidget focused;
    private boolean dragging;

    public double maxScrollY = 0;

    public ScrollableWidget(int x, int y, int width, int height, AbstractWidget... widgets) {
        super(x, y, width, height, Component.empty());
        Arrays.stream(widgets).forEach(widget -> {
            if (widget instanceof Child child) {
                child.setParent(this);
            }
            this.children().add(widget);
        });
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.children().reversed().forEach(widget -> widget.render(guiGraphics, mouseX, mouseY, partialTick));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || this.children().stream().anyMatch(child -> child.isMouseOver(mouseX, mouseY));
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
        if (this.maxScrollY > 0) {
            this.widgets.forEach(widget -> {
                if (widget instanceof Scrollable scrollable) {
                    scrollable.scroll(scrollY * 20);
                    scrollable.clamp(-maxScrollY, 0);
                }
            });
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public void clamp() {
        this.children().forEach(widget -> {
            if (widget instanceof Scrollable scrollable) {
                scrollable.clamp(-maxScrollY, 0);
            }
        });
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

    @Override
    public List<AbstractWidget> children() {
        return widgets;
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
    public AbstractWidget getFocused() {
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

            this.focused = (AbstractWidget) focused;
        }
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public void moveToTheTop() {

    }

    @Override
    public void requestRelayout() {
        ShatterLib.LOGGER.info("SCROLLABLE RELAYOUT");
    }
}
