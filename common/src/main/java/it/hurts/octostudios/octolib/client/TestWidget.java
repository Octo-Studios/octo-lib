package it.hurts.octostudios.octolib.client;

import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.particle.GalacticUIParticle;
import it.hurts.octostudios.octolib.client.particle.UIParticle;
import it.hurts.octostudios.octolib.util.OctoColor;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.Color;

public class TestWidget extends AbstractWidget {
    protected Screen screen;
    private Vector2f position = new Vector2f();
    private Vector2f scale = new Vector2f(1, 1);
    @Setter
    private OctoColor color = new OctoColor(1f,1f,1f,1f);

    public TransitionType transitionType;
    public EaseType easeType;
    public Tween tween;

    private Matrix4f renderPose = new Matrix4f();

    public Tween hoverTween;
    private boolean hasHovered = false;

    public TestWidget(int x, int y, TransitionType transitionType, EaseType easeType, Screen screen) {
        super(x, y, 8, 8, Component.literal(easeType.name() + "_" + transitionType.name()));
        this.transitionType = transitionType;
        this.easeType = easeType;
        this.screen = screen;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float partialTick) {
        float actualPartialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(position.x + this.getX(), position.y + this.getY(), 0);

        guiGraphics.pose().translate(this.width/2f, this.height/2f, 0);
        guiGraphics.pose().scale(scale.x, scale.y, 1);
        guiGraphics.pose().translate(-this.width/2f, -this.height/2f, 0);

        this.renderPose = new Matrix4f(guiGraphics.pose().last().pose());
        guiGraphics.renderOutline(0, 0, this.getWidth(), this.getHeight(), color.getARGB());
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

            hoverTween = Tween.create().setTransitionType(TransitionType.QUART).setEase(EaseType.EASE_OUT);
            hoverTween.tweenProperty(this, "scale", new Vector2f(1.25f, 1.25f), 0.2);
        } else if (!hovered && hasHovered) {
            hasHovered = false;
            if (hoverTween != null) {
                hoverTween.kill();
            }

            hoverTween = Tween.create().setTransitionType(TransitionType.QUART).setEase(EaseType.EASE_OUT);
            hoverTween.tweenProperty(this, "scale", new Vector2f(1f, 1f), 0.2);
        }

        return hovered;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (tween != null) {
            tween.kill();
        }

        Runnable spawnParticle = () -> {
            UIParticle uiParticle = new GalacticUIParticle(20f, 1, this.getX() + position.x, this.getY() + position.y, UIParticle.Layer.SCREEN, 1f);
            uiParticle.setScreen(screen);
            uiParticle.instantiate();
        };

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

        tween.tweenMethod(this::setColor, OctoColor.GREEN, OctoColor.WHITE, 0.5).setTransitionType(TransitionType.LINEAR);
        tween.parallel().tweenProperty(this, "position", new Vector2f(10, 10), 0.5);
        tween.parallel().tweenRunnable(spawnParticle);
        tween.tweenInterval(0.5);
        tween.tweenMethod(this::setColor, OctoColor.RED, OctoColor.WHITE, 0.5).setTransitionType(TransitionType.LINEAR);
        tween.parallel().tweenProperty(this, "position", new Vector2f(0, 0), 0.5);
        tween.parallel().tweenRunnable(spawnParticle);
        tween.tweenInterval(0.5);

        super.onClick(mouseX, mouseY);
    }
}
