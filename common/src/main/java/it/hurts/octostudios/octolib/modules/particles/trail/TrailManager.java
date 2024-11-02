package it.hurts.octostudios.octolib.modules.particles.trail;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Queue;

public class TrailManager {
    
    static long lastTick = 0;
    @Getter
    static Queue<TrailProvider> trails = new ArrayDeque<>();
    static IdentityHashMap<TrailProvider, TrailBuffer> map = new IdentityHashMap<>();
    
    public static void clientTick(ClientLevel level) {
        long time = level.getDayTime();
        
        if (time == lastTick)
            return;
        lastTick = time;
        
        var iterator = trails.iterator();
        while (iterator.hasNext()) {
            var p = iterator.next();
            
            if (!p.isAlive() && p.shouldBeRemoved()) {
                map.remove(p);
                iterator.remove();
                return;
            }
            
            if (time % p.frequency() == 0) {
                TrailBuffer buffer = map.get(p);
                if (p.isAlive()) buffer.write(p.getPointPosition(Minecraft.getInstance()
                        .getTimer().getGameTimeDeltaTicks()));
                else buffer.remove();
            }
        }
    }
    
    public static TrailBuffer getOrCreateBuffer(TrailProvider trailProvider) {
        if (map.containsKey(trailProvider))
            return map.get(trailProvider);
        
        var buffer = trailProvider.createBuffer();
        map.put(trailProvider, buffer);
        return buffer;
    }
    
    public static void registerTrail(TrailProvider trailProvider) {
        if (map.containsKey(trailProvider))
            return;
        
        trails.add(trailProvider);
        map.put(trailProvider, trailProvider.createBuffer());
    }
    
}
