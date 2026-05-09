package it.hurts.shatterbyte.byteapi.client.config.widget;
import it.hurts.shatterbyte.byteapi.client.animation.Tween;
import it.hurts.shatterbyte.byteapi.client.animation.easing.EaseType;
import it.hurts.shatterbyte.byteapi.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.byteapi.client.config.UIElements;
import it.hurts.shatterbyte.byteapi.client.screen.widget.Child;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    private static final int SCROLLBAR_WIDTH = 4;
    private static final int SCROLLBAR_MIN_THUMB_HEIGHT = 14;
    private static final int SCROLLBAR_TRACK_COLOR = 0x33111116;
    private static final int SCROLLBAR_THUMB_COLOR = 0xffffffff;
    private static final int SCROLLBAR_THUMB_HOVER_COLOR = 0xffdddddd;
    private static final int SCROLLBAR_THUMB_DRAG_COLOR = 0xffbbbbbb;
    private static final double SCROLL_STEP = 72.0;
    private static final double SCROLL_TWEEN_DURATION = 0.12;
    private static final double SCROLL_TO_TWEEN_DURATION = 0.16;

    List<AbstractWidget> widgets = new ArrayList<>();
    private @Nullable AbstractWidget focused;
    private boolean dragging;
    private boolean scrollbarDragging;
    private double scrollbarDragOffsetY;
    private boolean pinScrollbarToScreenRight;
    private int scrollbarRightInset;
    private Tween scrollTween = Tween.create();
    private double scrollAnimationTarget;
    private boolean hasScrollAnimationTarget;

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
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());
        this.children().reversed().forEach(widget -> widget.extractRenderState(guiGraphics, mouseX, mouseY, partialTick));
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
            this.stopScrollTween();
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
            this.stopScrollTween();
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

        boolean scrolled = animateScrollBy(scrollY * SCROLL_STEP);

        return scrolled || childHandled || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public void clamp() {
        this.stopScrollTween();
        this.children().forEach(widget -> {
            if (widget instanceof Scrollable scrollable) {
                scrollable.clamp(-maxScrollY, 0);
            }
        });
        this.syncScrollAnimationTargetToCurrent();
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
        this.stopScrollTween();
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

    private void renderScrollbar(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
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

        UIElements.SCROLLBAR_THINGY.render(guiGraphics, RenderPipelines.GUI_TEXTURED, x, thumbTop, SCROLLBAR_WIDTH, thumbHeight, thumbColor);
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
            this.syncScrollAnimationTargetToCurrent();
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
        this.syncScrollAnimationTargetToCurrent();
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

    public void scrollToOffset(double targetOffset, boolean smooth) {
        Scrollable scrollable = this.getScrollableChild();
        if (scrollable == null) {
            this.stopScrollTween();
            return;
        }

        double clampedTarget = Math.clamp(targetOffset, -this.maxScrollY, 0);
        if (!smooth || this.maxScrollY <= 0) {
            this.stopScrollTween();
            this.setScrollableOffsetAndClamp(clampedTarget);
            this.syncScrollAnimationTargetToCurrent();
            return;
        }

        this.scrollAnimationTarget = clampedTarget;
        this.hasScrollAnimationTarget = true;
        this.animateScrollTo(clampedTarget, SCROLL_TO_TWEEN_DURATION);
    }

    private boolean animateScrollBy(double delta) {
        Scrollable scrollable = this.getScrollableChild();
        if (scrollable == null) {
            return false;
        }

        double currentOffset = scrollable.getScrollOffset();
        if (!hasScrollAnimationTarget) {
            scrollAnimationTarget = currentOffset;
            hasScrollAnimationTarget = true;
        } else if (Math.abs(currentOffset - scrollAnimationTarget) > SCROLL_STEP) {
            scrollAnimationTarget = currentOffset;
        }

        scrollAnimationTarget = Math.clamp(scrollAnimationTarget + delta, -this.maxScrollY, 0);
        return this.animateScrollTo(scrollAnimationTarget, SCROLL_TWEEN_DURATION);
    }

    private boolean animateScrollTo(double to, double duration) {
        Scrollable scrollable = this.getScrollableChild();
        if (scrollable == null) {
            return false;
        }

        double from = scrollable.getScrollOffset();
        if (Math.abs(to - from) < 0.0001d) {
            return false;
        }

        scrollTween.kill();
        scrollTween = Tween.create();
        scrollTween.tweenMethod(this::setScrollableOffsetAndClamp, from, to, duration)
                .setEaseType(EaseType.EASE_OUT)
                .setTransitionType(TransitionType.SINE);
        scrollTween.start();
        return true;
    }

    private void setScrollableOffsetAndClamp(Double offset) {
        double target = offset == null ? 0 : offset;
        for (AbstractWidget widget : this.widgets) {
            if (widget instanceof Scrollable scrollable) {
                scrollable.setScrollOffset(target);
                scrollable.clamp(-this.maxScrollY, 0);
            }
        }
    }

    private void stopScrollTween() {
        scrollTween.kill();
        hasScrollAnimationTarget = false;
    }

    private void syncScrollAnimationTargetToCurrent() {
        Scrollable scrollable = this.getScrollableChild();
        if (scrollable == null) {
            hasScrollAnimationTarget = false;
            return;
        }

        scrollAnimationTarget = Math.clamp(scrollable.getScrollOffset(), -this.maxScrollY, 0);
        hasScrollAnimationTarget = true;
    }

    @Override
    public void playDownSound(SoundManager handler) {}
}
