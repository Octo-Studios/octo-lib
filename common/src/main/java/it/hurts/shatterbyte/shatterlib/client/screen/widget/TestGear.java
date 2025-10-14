package it.hurts.shatterbyte.shatterlib.client.screen.widget;

import it.hurts.shatterbyte.shatterlib.client.animation.Tween;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.EaseType;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import java.util.ArrayList;
import java.util.List;

public class TestGear extends AbstractWidget implements HasRenderMatrix, ContainerEventHandler {
    private Matrix3x2f renderMatrix;
    Tween tween = Tween.create();

    @Setter
    private float rot;

    List<GuiEventListener> children = new ArrayList<>();

    public TestGear(int x, int y) {
        super(x, y, 128, 128, Component.empty());
        this.children.add(new TestPin(16, 16, this));
    }

    @Override
    public Matrix3x2f getMatrix() {
        return this.renderMatrix;
    }

    @Override
    public void setMatrix(Matrix3x2f matrix) {
        this.renderMatrix = matrix;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.getX() + this.width/2f, this.getY() + this.height/2f);
        guiGraphics.pose().rotate((float) Math.toRadians(rot));
        guiGraphics.pose().translate(-this.width/2f - this.getX(), -this.height/2f - this.getY());
        this.setMatrix(new Matrix3x2f(guiGraphics.pose()));

        RenderUtils.renderOutline(guiGraphics,this.getX(), this.getY(), this.width, this.height, 0xffffffff);
        this.children().forEach(child -> {
            if (child instanceof Renderable renderable) {
                renderable.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        });

        guiGraphics.pose().popMatrix();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        boolean result = super.mouseClicked(event, isDoubleClick);
        if (!ContainerEventHandler.super.mouseClicked(event, isDoubleClick) && result) {
            tween.kill();
            tween = Tween.create();
            tween.tweenMethod(this::setRot, this.rot, this.rot + 60f, 0.75f).setEaseType(EaseType.EASE_OUT).setTransitionType(TransitionType.QUART);
            tween.start();
            return true;
        };
        return false;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean isDragging) {

    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return null;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {

    }
}
