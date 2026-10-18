package it.hurts.shatterbyte.shatterlib.module.particle.trail;

import it.hurts.shatterbyte.shatterlib.module.particle.RenderBuffer;
import net.minecraft.world.phys.Vec3;

public interface TrailBuffer extends Iterable<TrailSample>, RenderBuffer<TrailProvider, TrailBuffer> {

    void write(Vec3 vec3, double time);

    TrailSample peekFirst();

    int size();

    void remove();

    void clear();

    void renderTick(TrailProvider provider, float partialTick);

    @Override
    default void tick(TrailProvider provider) {

    }
}
