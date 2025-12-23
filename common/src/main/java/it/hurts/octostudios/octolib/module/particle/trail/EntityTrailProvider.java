package it.hurts.octostudios.octolib.module.particle.trail;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class EntityTrailProvider<T extends Entity> extends TrailProvider {
    public T entity;

    public EntityTrailProvider(T entity) {
        this.entity = entity;
    }

    @Override
    public Vec3 getTrailPosition(float partialTick) {
        return entity.getPosition(partialTick);
    }

    @Override
    public int getTrailUpdateFrequency() {
        return 1;
    }

    @Override
    public boolean isTrailAlive() {
        return entity.isAlive();
    }

    @Override
    public boolean isTrailGrowing() {
        var minMotion = Math.max(1e-4, getTrailMinSampleDistance() * 0.1);
        return entity.tickCount > 0 && entity.getDeltaMovement().length() > minMotion;
    }
}
