package it.hurts.octostudios.octolib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.client.animation.Tween;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.Color;

public class TestWidget extends AbstractWidget {
    public Vector2f position = new Vector2f();
    public Vector3f color = new Vector3f(1f,1f,1f);

    public TransitionType transitionType;
    public EaseType easeType;
    public Tween tween;

    public Tween hoverTween;
    private boolean hasHovered = false;

    public TestWidget(int x, int y, TransitionType transitionType, EaseType easeType) {
        super(x, y, 8, 8, Component.literal(easeType.name() + "_" + transitionType.name()));
        this.transitionType = transitionType;
        this.easeType = easeType;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float partialTick) {
        float actualPartialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        Color actualColor = new Color(color.x, color.y, color.z, 1f);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(position.x + this.getX(), position.y + this.getY(), 0);
        guiGraphics.renderOutline(0, 0, this.getWidth(), this.getHeight(), actualColor.getRGB());
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0);
        guiGraphics.renderOutline(-2, -12, 112, this.getHeight() + 14, 0x50ffffff);
        guiGraphics.pose().scale(0.75f, 1, 1);
        guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), 0, -10, 0x50ffffff, true);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean isHovered() {
        boolean hovered = super.isHovered();

        if (hovered && !hasHovered) {
            hasHovered = true;
            if (hoverTween != null) {
                hoverTween.kill();
            }

            hoverTween = Tween.create().setTransitionType(TransitionType.QUAD).setEase(EaseType.EASE_IN_OUT);
            hoverTween.tweenProperty(this, "color", new Vector3f(0f, 1f, 1f), 0.15);
        } else if (!hovered && hasHovered) {
            hasHovered = false;
            if (hoverTween != null) {
                hoverTween.kill();
            }

            hoverTween = Tween.create().setTransitionType(TransitionType.QUAD).setEase(EaseType.EASE_IN_OUT);
            hoverTween.tweenProperty(this, "color", new Vector3f(1f, 1f, 1f), 0.15);
        }

        return hovered;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (tween != null) {
            tween.kill();
        }

        tween = Tween.create().setTransitionType(transitionType).setEase(easeType).setLoops(3);
//        tween.tweenProperty(this, "position", new Vector2f(), 0);
//        tween.tweenProperty(this.position, "x", 100f, 0.5);
//        tween.tweenRunnable(() -> OctoLib.LOGGER.info("First runnable!"));
//        tween.tweenProperty(this.position, "y", 20f, 0.5);
//        tween.tweenRunnable(() -> OctoLib.LOGGER.info("Waiting for a second after this one..."));
//        tween.tweenInterval(1);
//        tween.tweenRunnable(() -> OctoLib.LOGGER.info("Yippie!"));
//        tween.tweenProperty(this, "color", Color.WHITE, 1)
//                .from(Color.GREEN)
//                .setTransitionType(TransitionType.LINEAR);

        tween.tweenProperty(this, "color", new Vector3f(1f, 1f, 1f), 0.5).from(new Vector3f(0f, 1f, 0f)).setTransitionType(TransitionType.LINEAR);
        tween.parallel().tweenProperty(this, "position", new Vector2f(10, 10), 0.5);
        tween.tweenInterval(0.5);
        tween.tweenProperty(this, "color", new Vector3f(1f, 1f, 1f), 0.5).from(new Vector3f(1f, 0f, 0f)).setTransitionType(TransitionType.LINEAR);
        tween.parallel().tweenProperty(this, "position", new Vector2f(0, 0), 0.5);
        tween.tweenInterval(0.5);

        super.onClick(mouseX, mouseY);
    }
}
