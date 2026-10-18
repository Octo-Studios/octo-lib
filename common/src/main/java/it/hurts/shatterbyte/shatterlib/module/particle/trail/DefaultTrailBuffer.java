package it.hurts.shatterbyte.shatterlib.module.particle.trail;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class DefaultTrailBuffer implements TrailBuffer {
    private static final int MAX_TICK_GAP = 20;

    private final Deque<TrailSample> points = new ArrayDeque<>();
    private final int maxSize;

    public DefaultTrailBuffer(int maxSize) {
        this.maxSize = Math.max(1, maxSize);
    }

    @Override
    public void write(Vec3 vec3, double time) {
        if (vec3 == null) return;

        points.addFirst(new TrailSample(vec3, time));

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

        var gameTime = level.getGameTime();
        var renderTime = gameTime - 1 + partialTick;
        var maxAge = Math.max(1.0, provider.getTrailLifetimeTicks());

        var newest = points.peekFirst();
        if (newest != null && (newest.time() > gameTime || gameTime - newest.time() > maxAge + MAX_TICK_GAP)) {
            clear();
        }

        if (provider.isTrailAlive() && provider.isTrailGrowing()) {
            newest = points.peekFirst();

            if (newest == null || newest.time() < gameTime - 1) {
                write(provider.getTrailPosition(0F), gameTime - 1);
            }

            if (points.peekFirst().time() < gameTime) {
                write(provider.getTrailPosition(1F), gameTime);
            }
        }

        var cutoff = renderTime - maxAge;
        while (points.size() > 1 && getSecondLast().time() < cutoff) {
            remove();
        }

        if (points.size() == 1 && points.peekLast().time() < cutoff) {
            remove();
        }

        while (points.size() > getHardLimit(provider)) {
            remove();
        }
    }

    private TrailSample getSecondLast() {
        var iterator = points.descendingIterator();
        iterator.next();

        return iterator.next();
    }

    @Override
    public TrailSample peekFirst() {
        return points.peekFirst();
    }

    @Override
    public void clear() {
        points.clear();
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
    public @NotNull Iterator<TrailSample> iterator() {
        return points.iterator();
    }
}
