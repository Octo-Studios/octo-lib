package it.hurts.octostudios.octolib.module.particle.trail;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ParticleTrailRegistry {
    public static final Map<ParticleType<? extends ParticleOptions>, Function<? extends Particle, ? extends ParticleTrailProvider<? extends Particle>>> classProviders = new HashMap<>();

    public static <T extends Particle, O extends ParticleOptions> void registerProvider(ParticleType<O> type, Function<T, ParticleTrailProvider<T>> factory) {
        classProviders.put(type, factory);
    }

    public static <T extends Particle> ParticleTrailProvider<T> getTrailProvider(ParticleType<? extends ParticleOptions> type, T particle) {
        Function<T, ParticleTrailProvider<T>> factory = (Function<T, ParticleTrailProvider<T>>) classProviders.get(type);
        if (factory == null) {
            return null;
        }

        return factory.apply(particle);
    }
}
