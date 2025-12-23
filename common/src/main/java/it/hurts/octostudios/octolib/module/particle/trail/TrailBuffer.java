package it.hurts.octostudios.octolib.module.particle.trail;

import it.hurts.octostudios.octolib.module.particle.RenderBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public interface TrailBuffer extends Iterable<TrailSample>, RenderBuffer<TrailProvider, TrailBuffer> {
    
    void write(TrailSample sample);
    
    default void write(Vec3 vec3, double time) {
        write(new TrailSample(vec3, time));
    }
    
    TrailSample peekFirst();
    
    TrailSample peekLast();
    
    int size();
    
    void remove();
    
    void removeFirst();
    
    void pruneOlderThan(double minTime);
    
    void trimToSize(int max);
    
    @Override
    default void tick(TrailProvider provider) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if (level == null) return;
        
        if (provider.getTrailMaxLength() <= 0) {
            trimToSize(0);
            return;
        }
        
        double now = level.getGameTime() + mc.getTimer().getGameTimeDeltaTicks();
        var maxAge = provider.getTrailEffectiveMaxAgeTicks(provider.isTrailGrowing());
        pruneOlderThan(now - maxAge);
        trimToSize(provider.getTrailMaxSamples());
    }
    
}
