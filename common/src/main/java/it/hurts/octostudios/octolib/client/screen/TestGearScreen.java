package it.hurts.octostudios.octolib.client.screen;

import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.client.particle.GalacticUIParticle;
import it.hurts.octostudios.octolib.client.particle.UIParticle;
import it.hurts.octostudios.octolib.client.screen.widget.TestGear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.Random;

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
        this.shouldTick = true;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new TestGear((int) (this.width/2f-64), (int) (this.height/2f-64)));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (shouldTick) {
            this.shouldTick = false;
            UIParticle particle = new GalacticUIParticle(1.25f, 40, mouseX, mouseY, UIParticle.Layer.SCREEN, 0);
            particle.setScreen(this);
            particle.rollVelocity = new Random().nextFloat(-5, 5);
            particle.instantiate();
        }

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
