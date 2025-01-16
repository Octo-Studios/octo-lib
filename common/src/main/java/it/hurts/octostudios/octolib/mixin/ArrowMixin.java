package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import it.hurts.octostudios.octolib.modules.particles.trail.TrailProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Arrow.class)
public class ArrowMixin implements TrailProvider {
    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
    public void aaa(EntityType entityType, Level level, CallbackInfo ci) {
        OctoRenderManager.registerProvider(this);
    }

    @Override
    public Vec3 getTrailPosition(float partialTick) {
        return ((Arrow) (Object) this).getPosition(partialTick);
    }

    @Override
    public int getTrailUpdateFrequency() {
        return 1;
    }

    @Override
    public boolean isTrailAlive() {
        return ((Arrow) (Object) this).isAlive();
    }

    @Override
    public boolean isTrailGrowing() {
        return ((Arrow) (Object) this).getDeltaMovement().length() > 0;
    }

    @Override
    public int getTrailMaxLength() {
        return 10;
    }

    @Override
    public int getTrailFadeInColor() {
        return 0x000000FF;
    }

    @Override
    public int getTrailFadeOutColor() {
        return 0xFFFF00FF;
    }

    @Override
    public double getTrailScale() {
        return 0.15;
    }
}
