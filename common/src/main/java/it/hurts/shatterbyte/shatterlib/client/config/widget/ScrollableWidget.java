package it.hurts.shatterbyte.shatterlib.client.config.widget;
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
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScrollableWidget extends AbstractWidget implements ContainerEventHandler, PathContainerWidget {
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_MIN_THUMB_HEIGHT = 14;
    private static final int SCROLLBAR_TRACK_COLOR = 0x33111116;
    private static final int SCROLLBAR_THUMB_COLOR = 0xffffffff;
    private static final int SCROLLBAR_THUMB_HOVER_COLOR = 0xffdddddd;
    private static final int SCROLLBAR_THUMB_DRAG_COLOR = 0xffbbbbbb;

    List<AbstractWidget> widgets = new ArrayList<>();
    private @Nullable AbstractWidget focused;
    private boolean dragging;
    private boolean scrollbarDragging;
    private double scrollbarDragOffsetY;
    private boolean pinScrollbarToScreenRight;
    private int scrollbarRightInset;

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
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());
        this.children().reversed().forEach(widget -> widget.render(guiGraphics, mouseX, mouseY, partialTick));
        guiGraphics.disableScissor();
        this.renderScrollbar(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY)
                || (this.hasScrollbar() && this.isInsideScrollbar(mouseX, mouseY));
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return ContainerEventHandler.super.nextFocusPath(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (event.button() == 0 && this.hasScrollbar() && this.isInsideScrollbar(event.x(), event.y())) {
            int thumbTop = this.getScrollbarThumbTop();
            int thumbHeight = this.getScrollbarThumbHeight();

            if (event.y() >= thumbTop && event.y() < thumbTop + thumbHeight) {
                this.scrollbarDragOffsetY = event.y() - thumbTop;
            } else {
                this.scrollbarDragOffsetY = thumbHeight / 2.0;
                this.setScrollFromThumbTop(event.y() - this.scrollbarDragOffsetY);
            }

            this.scrollbarDragging = true;
            return true;
        }

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
        if (this.scrollbarDragging && event.button() == 0) {
            this.scrollbarDragging = false;
            return true;
        }

        ContainerEventHandler.super.mouseReleased(event);
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if (this.scrollbarDragging) {
            this.setScrollFromThumbTop(event.y() - this.scrollbarDragOffsetY);
            return true;
        }

        ContainerEventHandler.super.mouseDragged(event, mouseX, mouseY);
        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean childHandled = ContainerEventHandler.super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        if (!this.isMouseOver(mouseX, mouseY) || scrollY == 0) {
            return childHandled || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        if (this.maxScrollY <= 0) {
            recalculateMaxScrollY();
        }

        if (this.maxScrollY <= 0) {
            return childHandled || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        boolean scrolled = false;
        for (AbstractWidget widget : this.widgets) {
            if (widget instanceof Scrollable scrollable) {
                scrollable.scroll(scrollY * 20);
                scrollable.clamp(-maxScrollY, 0);
                scrolled = true;
            }
        }

        return scrolled || childHandled || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
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
        for (AbstractWidget widget : this.children()) {
            if (widget instanceof DynamicallySized ds) {
                ds.repositionElements();
            }
        }

        recalculateMaxScrollY();
        this.clamp();
    }

    private void recalculateMaxScrollY() {
        int contentBottom = 0;

        for (AbstractWidget widget : this.children()) {
            int localY;
            if (widget instanceof Child<?> child) {
                localY = child.getLocalY();
            } else {
                localY = widget.getY() - this.getY();
            }

            contentBottom = Math.max(contentBottom, localY + widget.getHeight());
        }

        this.maxScrollY = Math.max(0, contentBottom - this.height);
    }

    private void renderScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!this.hasScrollbar()) {
            return;
        }

        int x = this.getScrollbarX();
        int y = this.getScrollbarY();
        int h = this.getScrollbarHeight();
        if (h <= 0) {
            return;
        }

        guiGraphics.fill(x, y, x + SCROLLBAR_WIDTH, y + h, SCROLLBAR_TRACK_COLOR);

        int thumbTop = this.getScrollbarThumbTop();
        int thumbHeight = this.getScrollbarThumbHeight();
        int thumbColor = SCROLLBAR_THUMB_COLOR;
        if (this.scrollbarDragging) {
            thumbColor = SCROLLBAR_THUMB_DRAG_COLOR;
        } else if (this.isInsideScrollbar(mouseX, mouseY)
                && mouseY >= thumbTop
                && mouseY < thumbTop + thumbHeight) {
            thumbColor = SCROLLBAR_THUMB_HOVER_COLOR;
        }

        UIElements.SCROLLBAR_THINGY.render(guiGraphics, RenderPipelines.GUI_TEXTURED, x+1, thumbTop, SCROLLBAR_WIDTH-2, thumbHeight, thumbColor);
    }

    private boolean hasScrollbar() {
        return this.maxScrollY > 0 && this.getScrollableChild() != null && this.getScrollbarHeight() > 0;
    }

    private int getScrollbarX() {
        if (this.pinScrollbarToScreenRight) {
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            return screenWidth - SCROLLBAR_WIDTH - Math.max(0, this.scrollbarRightInset);
        }

        return this.getX() + this.getWidth() - SCROLLBAR_WIDTH;
    }

    private int getScrollbarY() {
        return this.getY();
    }

    private int getScrollbarHeight() {
        return this.getHeight();
    }

    private int getScrollbarThumbHeight() {
        int trackHeight = this.getScrollbarHeight();
        if (trackHeight <= 0) {
            return 0;
        }

        double contentHeight = this.getHeight() + this.maxScrollY;
        if (contentHeight <= 0) {
            return trackHeight;
        }

        int size = (int) Math.round(trackHeight * (this.getHeight() / contentHeight));
        return Math.clamp(size, SCROLLBAR_MIN_THUMB_HEIGHT, trackHeight);
    }

    private int getScrollbarThumbTop() {
        Scrollable scrollable = this.getScrollableChild();
        if (scrollable == null || this.maxScrollY <= 0) {
            return this.getScrollbarY();
        }

        int trackY = this.getScrollbarY();
        int trackHeight = this.getScrollbarHeight();
        int thumbHeight = this.getScrollbarThumbHeight();
        int travel = Math.max(0, trackHeight - thumbHeight);
        if (travel == 0) {
            return trackY;
        }

        double progress = Math.clamp((-scrollable.getScrollOffset()) / this.maxScrollY, 0.0, 1.0);
        return trackY + (int) Math.round(progress * travel);
    }

    private boolean isInsideScrollbar(double mouseX, double mouseY) {
        int x = this.getScrollbarX();
        int y = this.getScrollbarY();
        int h = this.getScrollbarHeight();
        return mouseX >= x && mouseX < x + SCROLLBAR_WIDTH && mouseY >= y && mouseY < y + h;
    }

    private void setScrollFromThumbTop(double thumbTop) {
        if (this.getScrollableChild() == null || this.maxScrollY <= 0) {
            return;
        }

        int trackY = this.getScrollbarY();
        int trackHeight = this.getScrollbarHeight();
        int thumbHeight = this.getScrollbarThumbHeight();
        int travel = Math.max(0, trackHeight - thumbHeight);

        if (travel == 0) {
            for (AbstractWidget widget : this.widgets) {
                if (widget instanceof Scrollable scrollable) {
                    scrollable.setScrollOffset(0);
                    scrollable.clamp(-this.maxScrollY, 0);
                }
            }
            return;
        }

        double clampedTop = Math.clamp(thumbTop, trackY, trackY + travel);
        double progress = (clampedTop - trackY) / travel;
        double targetOffset = -progress * this.maxScrollY;

        for (AbstractWidget widget : this.widgets) {
            if (widget instanceof Scrollable scrollable) {
                scrollable.setScrollOffset(targetOffset);
                scrollable.clamp(-this.maxScrollY, 0);
            }
        }
    }

    private @Nullable Scrollable getScrollableChild() {
        for (AbstractWidget widget : this.widgets) {
            if (widget instanceof Scrollable scrollable) {
                return scrollable;
            }
        }

        return null;
    }

    public void setPinScrollbarToScreenRight(boolean pinScrollbarToScreenRight) {
        this.pinScrollbarToScreenRight = pinScrollbarToScreenRight;
    }

    public void setScrollbarRightInset(int scrollbarRightInset) {
        this.scrollbarRightInset = Math.max(0, scrollbarRightInset);
    }

    public int getScrollbarWidth() {
        return SCROLLBAR_WIDTH;
    }

    @Override
    public void playDownSound(SoundManager handler) {}
}
