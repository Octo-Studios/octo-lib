package it.hurts.shatterbyte.byteapi.client;

import it.hurts.shatterbyte.byteapi.client.animation.easing.EaseType;
import it.hurts.shatterbyte.byteapi.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.byteapi.client.animation.Tween;
import it.hurts.shatterbyte.byteapi.client.particle.GalacticUIParticle;
import it.hurts.shatterbyte.byteapi.client.particle.UIParticle;
import it.hurts.shatterbyte.byteapi.util.RenderUtils;
import it.hurts.shatterbyte.byteapi.util.ShatterColor;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

public class TestWidget extends AbstractWidget {
    protected Screen screen;
    private Vector2f position = new Vector2f();
    private Vector2f scale = new Vector2f(1, 1);
    @Setter
    private ShatterColor color = new ShatterColor(1f,1f,1f,1f);

    public TransitionType transitionType;
    public EaseType easeType;
    public Tween tween;

    private Matrix3x2f renderPose = new Matrix3x2f();

    public Tween hoverTween;
    private boolean hasHovered = false;

    public TestWidget(int x, int y, TransitionType transitionType, EaseType easeType, Screen screen) {
        super(x, y, 8, 8, Component.literal(easeType.name() + "_" + transitionType.name()));
        this.transitionType = transitionType;
        this.easeType = easeType;
        this.screen = screen;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float partialTick) {
        float actualPartialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(position.x + this.getX(), position.y + this.getY());

        guiGraphics.pose().translate(this.width/2f, this.height/2f);
        guiGraphics.pose().scale(scale.x, scale.y);
        guiGraphics.pose().translate(-this.width/2f, -this.height/2f);

        this.renderPose = new Matrix3x2f(guiGraphics.pose());
        RenderUtils.renderOutline(guiGraphics,0, 0, this.getWidth(), this.getHeight(), color.getARGB());
        guiGraphics.pose().popMatrix();

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.getX(), this.getY());
        RenderUtils.renderOutline(guiGraphics,-2, -12, 112, this.getHeight() + 14, 0x50ffffff);
        guiGraphics.pose().scale(0.75f, 1);
        guiGraphics.text(Minecraft.getInstance().font, this.getMessage(), 0, -10, 0x50ffffff, true);
        guiGraphics.pose().popMatrix();
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
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        if (tween != null) {
            tween.kill();
        }

        Runnable spawnParticle = () -> {
            UIParticle uiParticle = new GalacticUIParticle(20f, 1, this.getX() + position.x, this.getY() + position.y, UIParticle.Layer.SCREEN, 1f);
            uiParticle.setScreen(screen);
            uiParticle.instantiate();
        };

        tween = Tween.create().setTransitionType(transitionType).setEase(easeType).setLoops(3);
        tween.tweenMethod(this::setColor, ShatterColor.GREEN, ShatterColor.WHITE, 0.5).setTransitionType(TransitionType.LINEAR);
        tween.parallel().tweenProperty(this, "position", new Vector2f(10, 10), 0.5);
        tween.parallel().tweenRunnable(spawnParticle);
        tween.tweenInterval(0.5);
        tween.tweenMethod(this::setColor, ShatterColor.RED, ShatterColor.WHITE, 0.5).setTransitionType(TransitionType.LINEAR);
        tween.parallel().tweenProperty(this, "position", new Vector2f(0, 0), 0.5);
        tween.parallel().tweenRunnable(spawnParticle);
        tween.tweenInterval(0.5);

        super.onClick(event, isDoubleClick);
    }
}
