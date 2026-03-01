package it.hurts.shatterbyte.shatterlib.module.particle.trail;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DefaultTrailBuffer implements TrailBuffer {
    private static final double EPSILON = 1.0E-6;

    private final Deque<TrailPoint> points = new ArrayDeque<>();
    private final int maxSize;
    private double lastFrameTime = Double.NaN;

    public DefaultTrailBuffer(int maxSize) {
        this.maxSize = Math.max(1, maxSize);
    }

    @Override
    public void write(Vec3 vec3) {
        if (vec3 == null) return;

        points.addFirst(new TrailPoint(vec3, 0));

        while (points.size() > getHardLimit()) {
            remove();
        }
    }

    @Override
    public void renderTick(TrailProvider provider, float partialTick) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            clear();
            return;
        }

        var now = level.getGameTime() + partialTick;
        if (Double.isNaN(lastFrameTime)) {
            lastFrameTime = now;
        }

        var deltaTicks = now - lastFrameTime;
        lastFrameTime = now;

        if (deltaTicks > 0 && deltaTicks < 5) {
            for (var point : points) {
                point.ageTicks += deltaTicks;
            }
        }

        if (provider.isTrailAlive() && provider.isTrailGrowing()) {
            var currentPos = provider.getTrailPosition(partialTick);
            var head = points.peekFirst();

            if (head == null) {
                write(currentPos);
            } else {
                var minDistance = Math.max(EPSILON, provider.getTrailSampleMinDistance());
                var minDistanceSqr = minDistance * minDistance;

                if (head.position.distanceToSqr(currentPos) >= minDistanceSqr) {
                    write(currentPos);
                } else {
                    head.position = currentPos;
                    head.ageTicks = 0;
                }
            }
        }

        var maxAge = Math.max(1.0, provider.getTrailLifetimeTicks());
        while (!points.isEmpty() && points.peekLast().ageTicks > maxAge) {
            remove();
        }

        while (points.size() > getHardLimit(provider)) {
            remove();
        }
    }

    @Override
    public void clear() {
        points.clear();
        lastFrameTime = Double.NaN;
    }

    private int getHardLimit() {
        return Math.max(8, maxSize * 8);
    }

    private int getHardLimit(TrailProvider provider) {
        return Math.max(getHardLimit(), provider.getTrailMaxPointCount());
    }

    @Override
    public int size() {
        return points.size();
    }

    @Override
    public void remove() {
        if (!points.isEmpty()) {
            points.removeLast();
        }
    }

    @Override
    public @NotNull Iterator<Vec3> iterator() {
        var delegate = points.iterator();

        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public Vec3 next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return delegate.next().position;
            }
        };
    }

    private static class TrailPoint {
        private Vec3 position;
        private double ageTicks;

        private TrailPoint(Vec3 position, double ageTicks) {
            this.position = position;
            this.ageTicks = ageTicks;
        }
    }
}
