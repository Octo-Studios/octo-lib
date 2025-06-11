package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.module.particle.OctoRenderManager;
import it.hurts.octostudios.octolib.module.particle.trail.TrailProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public class ArrowMixin implements TrailProvider {
    @Inject(method = "<init>*", at = @At("RETURN"))
    public void aaa(EntityType entityType, Level level, CallbackInfo ci) {
        if (level.isClientSide())
            OctoRenderManager.registerProvider(this);
    }

    @Override
    public Vec3 getTrailPosition(float partialTick) {
        return ((Entity) (Object) this).getPosition(partialTick);
    }

    @Override
    public int getTrailUpdateFrequency() {
        return 1;
    }

    @Override
    public boolean isTrailAlive() {
        return ((Entity) (Object) this).isAlive();
    }

    @Override
    public boolean isTrailGrowing() {
        var entity = ((Entity) (Object) this);

        return entity.tickCount > 0 && entity.getDeltaMovement().length() > 0;
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
