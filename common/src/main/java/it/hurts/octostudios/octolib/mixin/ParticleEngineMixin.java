package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.module.particle.OctoRenderManager;
import it.hurts.octostudios.octolib.module.particle.trail.ParticleTrailProvider;
import it.hurts.octostudios.octolib.module.particle.trail.ParticleTrailRegistry;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
    @Inject(method = "createParticle", at = @At("RETURN"))
    private void injectTrail(ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir) {
        Particle particle = cir.getReturnValue();
        if (particle == null) {
            return;
        }

        ParticleTrailProvider<Particle> trail = ParticleTrailRegistry.getTrailProvider(particleData.getType(), particle);
        if (trail != null) {
            OctoRenderManager.registerProvider(trail);
        }
    }
}
