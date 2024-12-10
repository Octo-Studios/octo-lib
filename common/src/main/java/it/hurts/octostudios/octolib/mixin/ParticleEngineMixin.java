//package it.hurts.octostudios.octolib.mixin;
//
//import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
//import it.hurts.octostudios.octolib.modules.particles.trail.TrailProvider;
//import net.minecraft.client.particle.Particle;
//import net.minecraft.client.particle.ParticleEngine;
//import net.minecraft.core.particles.ParticleOptions;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(ParticleEngine.class)
//public class ParticleEngineMixin {
//    @Inject(method = "createParticle", at = @At("RETURN"))
//    public void createParticle(ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir) {
//        OctoRenderManager.registerProvider((TrailProvider) cir.getReturnValue());
//    }
//}