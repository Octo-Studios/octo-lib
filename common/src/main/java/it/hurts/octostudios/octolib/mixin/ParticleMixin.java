package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.modules.particles.trail.TrailProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Particle.class)
public class ParticleMixin implements TrailProvider {
    @Shadow
    protected double x;
    @Shadow
    protected double y;
    @Shadow
    protected double z;

    @Override
    public int getTrailMaxLength() {
        return 5;
    }

    @Override
    public int getTrailUpdateFrequency() {
        return 1;
    }

    @Override
    public double getTrailScale() {
        return 0.05D;
    }

    @Override
    public Vec3 getTrailPosition(float partialTicks) {
        return new Vec3(x, y, z);
    }

    @Override
    public boolean isTrailGrowing() {
        return true;
    }

    @Override
    public boolean isTrailAlive() {
        var particle = (Particle) (Object) this;

        return particle.isAlive();
    }

    @Override
    public int getTrailFadeInColor() {
        return 0xFFFFFFFF;
    }

    @Override
    public int getTrailFadeOutColor() {
        return 0xFFFFFFFF;
    }
}