package it.hurts.octostudios.octolib.client;

import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.animation.easing.EaseType;
import it.hurts.octostudios.octolib.client.animation.easing.TransitionType;
import it.hurts.octostudios.octolib.client.particle.GalacticUIParticle;
import it.hurts.octostudios.octolib.client.particle.UIParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Vector2d;

public class TestScreen extends Screen {
    private int ticker;
    private boolean down;

    public Vector2d squeeze = new Vector2d(1,1);

    public TestScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        int x = 8;
        int y = 16;
        for (TransitionType transitionType : TransitionType.values()) {
            for (EaseType easeType : EaseType.values()) {
                this.addRenderableWidget(new TestWidget(x, y, transitionType, easeType, this));
                y += 30;

                if (y + 30 > this.height) {
                    x += 120;
                    y = 16;
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.width/2f, this.height/2f, 0);
        guiGraphics.pose().scale((float) this.squeeze.x, (float) this.squeeze.y, 1);
        guiGraphics.pose().translate(-this.width/2f, -this.height/2f, 0);

        UIParticle uiParticle = new GalacticUIParticle(20f, 2, i, j, UIParticle.Layer.SCREEN, 1f);
        uiParticle.setScreen(this);
        uiParticle.instantiate();

        super.render(guiGraphics, i, j, f);
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                String.valueOf(ticker),
                this.width - Minecraft.getInstance().font.width(String.valueOf(ticker)) - 4,
                4, 0xffffffff, true);
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                String.valueOf(f),
                this.width - Minecraft.getInstance().font.width(String.valueOf(f)) - 4,
                this.height - 10, 0xffffffff, true);

        guiGraphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        if (!result && button == 1) {
            for (GuiEventListener g : this.children()) {
                if (g instanceof TestWidget widget) {
                    widget.onClick(mouseX, mouseY);
                }
            }
        } else if (!result && button == 0) {
            //this.squeeze = new Vector2d(1,1);

            var tween = Tween.create().setTransitionType(TransitionType.SINE).setParallel(true);
            tween.tweenProperty(this, "squeeze.y", 0.9, 0.15).setEaseType(EaseType.EASE_OUT);
            tween.tweenProperty(this, "squeeze.x", 0.9, 0.15).setEaseType(EaseType.EASE_IN);
            tween.tweenProperty(this, "squeeze.y", 1, 0.15).setDelay(0.15).setEaseType(EaseType.EASE_OUT);
            tween.tweenProperty(this, "squeeze.x", 1, 0.15).setDelay(0.15).setEaseType(EaseType.EASE_IN);
        }
        return result;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void onClose() {
//        for (GuiEventListener g : this.children()) {
//            if (g instanceof TestWidget widget && widget.animator != null) {
//                widget.animator.stop();
//            }
//        }

        super.onClose();
    }
}
