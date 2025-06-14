package it.hurts.octostudios.octolib.client.screen.widget;

import com.mojang.math.Axis;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class TestGear extends AbstractWidget implements HasRenderMatrix, ContainerEventHandler {
    private Matrix4f renderMatrix;

    @Setter
    private float rot;

    List<GuiEventListener> children = new ArrayList<>();

    public TestGear(int x, int y) {
        super(x, y, 128, 128, Component.empty());
        this.children.add(new TestPin(16, 16, this));
    }

    @Override
    public Matrix4f getMatrix() {
        return this.renderMatrix;
    }

    @Override
    public void setMatrix(Matrix4f matrix) {
        this.renderMatrix = matrix;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX() + this.width/2f, this.getY() + this.height/2f, 0);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(rot));
        guiGraphics.pose().translate(-this.width/2f - this.getX(), -this.height/2f - this.getY(), 0);
        this.setMatrix(new Matrix4f(guiGraphics.pose().last().pose()));

        guiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, 0xffffffff);
        this.children().forEach(child -> {
            if (child instanceof Renderable renderable) {
                renderable.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        });

        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX,mouseY, button);
        if (!ContainerEventHandler.super.mouseClicked(mouseX, mouseY, button) && result) {
            Tween tween = Tween.create();
            tween.tweenMethod(this::setRot, this.rot, this.rot + 60f, 0.75f).setEaseType(EaseType.EASE_OUT).setTransitionType(TransitionType.QUART);
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
