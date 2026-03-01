package it.hurts.shatterbyte.shatterlib.module.particle.trail;

import it.hurts.shatterbyte.shatterlib.module.particle.RenderBuffer;
import net.minecraft.world.phys.Vec3;

public interface TrailBuffer extends Iterable<Vec3>, RenderBuffer<TrailProvider, TrailBuffer> {

    void write(Vec3 vec3);

    int size();

    void remove();

    void clear();

    void renderTick(TrailProvider provider, float partialTick);

    @Override
    default void tick(TrailProvider provider) {

    }
}
