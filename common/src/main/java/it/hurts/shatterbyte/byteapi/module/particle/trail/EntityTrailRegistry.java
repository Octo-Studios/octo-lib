package it.hurts.shatterbyte.byteapi.module.particle.trail;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EntityTrailRegistry {
    public static final Map<EntityType<? extends Entity>, Function<? extends Entity, ? extends EntityTrailProvider<? extends Entity>>> classProviders = new HashMap<>();

    public static <T extends Entity> void registerProvider(EntityType<T> type, Function<T, EntityTrailProvider<T>> factory) {
        classProviders.put(type, factory);
    }

    public static <T extends Entity> EntityTrailProvider<T> getTrailProvider(T entity) {
        Function<T, EntityTrailProvider<T>> factory = (Function<T, EntityTrailProvider<T>>) classProviders.get(entity.getType());
        if (factory == null) {
            return null;
        }

        return factory.apply(entity);
    }
}
