package it.hurts.octostudios.octolib.modules.particles.trail;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class DefaultTrailBuffer implements TrailBuffer {

    Deque<Vec3> points = new ArrayDeque<>();
    final int maxSize;

    public DefaultTrailBuffer(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void write(Vec3 vec3) {
        if (size() >= maxSize)
            remove();

        points.push(vec3);
    }

    @Override
    public int size() {
        return points.size();
    }

    @Override
    public void remove() {
        points.removeLast();
    }

    @Override
    public @NotNull Iterator<Vec3> iterator() {
        return points.iterator();
    }

}
