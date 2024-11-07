package it.hurts.octostudios.octolib.modules.particles.trail;

import it.hurts.octostudios.octolib.modules.particles.RenderBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public interface TrailBuffer extends Iterable<Vec3>, RenderBuffer<TrailProvider, TrailBuffer> {
    
    void write(Vec3 vec3);
    
    int size();
    
    void remove();
    
    @Override
    default void tick(TrailProvider provider) {
        var iterator = iterator();
        var position = provider.getRenderPosition(Minecraft.getInstance().getTimer().getGameTimeDeltaTicks());
        
        if (provider.isAlive() && (!iterator.hasNext()
                || iterator.next().distanceTo(position) >= provider.getMinDistanceDelta() / (float) provider.getBufferTickInterval()))
            write(position);
        else remove();
    }
    
}
