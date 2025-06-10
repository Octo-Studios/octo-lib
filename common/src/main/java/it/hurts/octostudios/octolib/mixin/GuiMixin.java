package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.client.particle.ParticleSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "render", at = @At("RETURN"))
    private void renderGuiParticles(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        ParticleSystem.renderGuiParticles(guiGraphics);
    }
}
