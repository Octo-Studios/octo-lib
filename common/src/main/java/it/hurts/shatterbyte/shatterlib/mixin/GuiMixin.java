package it.hurts.shatterbyte.shatterlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.hurts.shatterbyte.shatterlib.client.particle.ParticleSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void renderGuiParticles(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo ci, @Local GuiGraphicsExtractor graphics) {
        ParticleSystem.renderGuiParticles(graphics, deltaTracker.getGameTimeDeltaPartialTick(true));
    }
}
