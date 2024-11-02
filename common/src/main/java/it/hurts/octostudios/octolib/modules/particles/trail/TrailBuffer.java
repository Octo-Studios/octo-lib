package it.hurts.octostudios.octolib.modules.particles.trail;

import net.minecraft.world.phys.Vec3;

public interface TrailBuffer extends Iterable<Vec3> {
    
    void write(Vec3 vec3);
    
    int maxSize();
    
    int size();
    
    void remove();
    
}
