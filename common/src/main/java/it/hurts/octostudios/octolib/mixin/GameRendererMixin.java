package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.client.particle.ParticleSystem;
import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isGameLoadFinished()Z"))
    private void renderTick(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        OctoRenderManager.clientRenderTick();
    }
}
