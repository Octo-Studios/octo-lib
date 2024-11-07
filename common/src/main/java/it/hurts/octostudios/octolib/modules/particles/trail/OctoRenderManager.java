package it.hurts.octostudios.octolib.modules.particles.trail;

import it.hurts.octostudios.octolib.modules.particles.RenderBuffer;
import it.hurts.octostudios.octolib.modules.particles.RenderProvider;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Queue;

public class OctoRenderManager {
    
    static long lastTick = 0;
    @Getter
    static Queue<RenderProvider<?, ?>> providers = new ArrayDeque<>();
    static IdentityHashMap<RenderProvider<?, ?>, RenderBuffer<?, ?>> map = new IdentityHashMap<>();
    
    public static void clientTick(ClientLevel level) {
        long time = level.getDayTime();
        
        if (time == lastTick)
            return;
        lastTick = time;
        
        var iterator = providers.iterator();
        while (iterator.hasNext()) {
            var p = iterator.next();
            
            if (!p.isAlive()) {
                map.remove(p);
                iterator.remove();
                return;
            }
            
            if (time % p.getBufferTickInterval() == 0) {
                RenderBuffer buffer = map.get(p);
                buffer.tick(p);
            }
        }
    }
    
    public static <B extends RenderBuffer<P, B>, P extends RenderProvider<P, B>> B getOrCreateBuffer(P provider) {
        if (map.containsKey(provider))
            return (B) map.get(provider);
        
        var buffer = provider.createBuffer();
        map.put(provider, buffer);
        return buffer;
    }
    
    public static void registerTrail(TrailProvider trailProvider) {
        if (map.containsKey(trailProvider))
            return;
        
        providers.add(trailProvider);
        map.put(trailProvider, trailProvider.createBuffer());
    }
    
}
