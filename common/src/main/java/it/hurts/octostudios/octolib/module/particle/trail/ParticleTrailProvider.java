package it.hurts.octostudios.octolib.module.particle.trail;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;

public abstract class ParticleTrailProvider<T extends Particle> extends TrailProvider {
    public T particle;

    public ParticleTrailProvider(T particle) {
        this.particle = particle;
    }

    @Override
    public Vec3 getTrailPosition(float partialTick) {
        var box = particle.getBoundingBox();
        return new Vec3((box.minX + box.maxX) * 0.5, box.minY, (box.minZ + box.maxZ) * 0.5);
    }

    @Override
    public int getTrailUpdateFrequency() {
        return 1;
    }

    @Override
    public boolean isTrailAlive() {
        return particle.isAlive();
    }
}
