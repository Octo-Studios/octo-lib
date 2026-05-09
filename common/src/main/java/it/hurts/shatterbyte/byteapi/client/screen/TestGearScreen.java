package it.hurts.shatterbyte.byteapi.client.screen;

import it.hurts.shatterbyte.byteapi.client.animation.Tween;
import it.hurts.shatterbyte.byteapi.client.particle.GalacticUIParticle;
import it.hurts.shatterbyte.byteapi.client.particle.UIParticle;
import it.hurts.shatterbyte.byteapi.client.screen.widget.TestGear;
import it.hurts.shatterbyte.byteapi.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.util.Random;

import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;

public class TestGearScreen extends Screen {
    Tween tween = Tween.create().setLoops(-1);

    public TestGearScreen() {
        super(Component.empty());

        tween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOODEN_BUTTON_CLICK_ON, 2F)));
        tween.tweenInterval(0.5);
        tween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOODEN_BUTTON_CLICK_OFF, 1.6F)));
        tween.tweenInterval(0.5);
        tween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOODEN_BUTTON_CLICK_OFF, 1.6F)));
        tween.tweenInterval(0.5);
        tween.tweenRunnable(() -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOODEN_BUTTON_CLICK_OFF, 1.6F)));
        tween.tweenInterval(0.5);
        tween.start();
    }

    boolean shouldTick;

    @Override
    public void tick() {
        super.tick();
        if (Minecraft.getInstance().player.tickCount % 20 == 0) this.shouldTick = true;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new TestGear((int) (this.width/2f-64), (int) (this.height/2f-64)));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        //super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        if (shouldTick) {
            this.shouldTick = false;
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(mouseX,mouseY);
            UIParticle particle = new GalacticUIParticle(1.25f, 40, 0, 0, UIParticle.Layer.SCREEN, 0);
            particle.setScreen(this);
            particle.setRollVelocity(new Random().nextFloat(-5, 5));
            particle.setMatrix(guiGraphics.pose());
            particle.instantiate();
            guiGraphics.pose().popMatrix();
        }

        RenderUtils.renderTilingTexture(GUI_TEXTURED, Identifier.withDefaultNamespace("textures/particle/sga_").withSuffix("a.png"), guiGraphics, 10, 10, 0, 0, 8, 8, 80, 80, 0xffffffff, true, false);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (tween != null) {
            tween.kill();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
