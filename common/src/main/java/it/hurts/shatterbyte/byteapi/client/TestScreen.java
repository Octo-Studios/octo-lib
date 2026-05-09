package it.hurts.shatterbyte.byteapi.client;

import it.hurts.shatterbyte.byteapi.client.animation.Tween;
import it.hurts.shatterbyte.byteapi.client.animation.easing.EaseType;
import it.hurts.shatterbyte.byteapi.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.byteapi.client.particle.GalacticUIParticle;
import it.hurts.shatterbyte.byteapi.client.particle.UIParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
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
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.width/2f, this.height/2f);
        guiGraphics.pose().scale((float) this.squeeze.x, (float) this.squeeze.y);
        guiGraphics.pose().translate(-this.width/2f, -this.height/2f);

        UIParticle uiParticle = new GalacticUIParticle(20f, 2, i, j, UIParticle.Layer.SCREEN, 1f);
        uiParticle.setScreen(this);
        uiParticle.instantiate();

        super.extractRenderState(guiGraphics, i, j, f);
        guiGraphics.text(
                Minecraft.getInstance().font,
                String.valueOf(ticker),
                this.width - Minecraft.getInstance().font.width(String.valueOf(ticker)) - 4,
                4, 0xffffffff, true);
        guiGraphics.text(
                Minecraft.getInstance().font,
                String.valueOf(f),
                this.width - Minecraft.getInstance().font.width(String.valueOf(f)) - 4,
                this.height - 10, 0x33ffffff, true);

        guiGraphics.pose().popMatrix();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        boolean result = super.mouseClicked(event, isDoubleClick);
        if (!result && event.button() == 1) {
            for (GuiEventListener g : this.children()) {
                if (g instanceof TestWidget widget) {
                    widget.onClick(event, isDoubleClick);
                }
            }
        } else if (!result && event.button() == 0) {
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
