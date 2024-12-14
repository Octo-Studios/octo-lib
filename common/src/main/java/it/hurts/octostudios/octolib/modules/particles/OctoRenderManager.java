package it.hurts.octostudios.octolib.modules.particles;

import it.hurts.octostudios.octolib.modules.particles.trail.TrailProvider;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;
import org.apache.logging.log4j.util.Cast;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Queue;
import java.util.WeakHashMap;

public class OctoRenderManager {
    
    static long lastTick = 0;
    @Getter
    static Queue<RenderProvider<?, ?>> providers = new ArrayDeque<>();
    static WeakHashMap<RenderProvider<?, ?>, RenderBuffer<?, ?>> map = new WeakHashMap<>();
    
    public static void clientTick(ClientLevel level) {
        long time = level.getGameTime();
        
        if (time == lastTick)
            return;
        lastTick = time;
        
        var iterator = providers.iterator();
        while (iterator.hasNext()) {
            var p = iterator.next();
            RenderBuffer buffer = getOrCreateBuffer(Cast.cast(p));
            
            if (!p.shouldRender(Cast.cast(buffer))) {
                map.remove(p);
                iterator.remove();
                continue;
            }
            
            if (time % p.getUpdateFrequency() == 0) {
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
    
    public static <B extends RenderBuffer<P, B>, P extends RenderProvider<P, B>> void registerProvider(P provider) {
        if (map.containsKey(provider))
            return;

        providers.add(provider);
        map.put(provider, provider.createBuffer());
    }
    
}
