package it.hurts.octostudios.octolib.module.particle.trail;

import net.minecraft.client.particle.Particle;

public class TestFlameParticleTrail extends ParticleTrailProvider<Particle> {
    public TestFlameParticleTrail(Particle particle) {
        super(particle);
    }

    @Override
    public int getTrailUpdateFrequency() {
        return 1;
    }

    @Override
    public int getTrailMaxLength() {
        return 8;
    }

    @Override
    public int getTrailFadeInColor() {
        return 0x0000FFFF;
    }

    @Override
    public int getTrailFadeOutColor() {
        return 0xFFFF7A00;
    }

    @Override
    public double getTrailScale() {
        return 0.06;
    }
}
