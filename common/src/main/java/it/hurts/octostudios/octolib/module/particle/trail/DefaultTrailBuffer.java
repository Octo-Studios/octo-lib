package it.hurts.octostudios.octolib.module.particle.trail;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

@Data
public class DefaultTrailBuffer<T> implements TrailBuffer {
    private Deque<TrailSample> points = new ArrayDeque<>();

    private final int maxSize;

    public DefaultTrailBuffer(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void write(TrailSample sample) {
        if (size() >= maxSize) remove();
        points.addFirst(sample);
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
    public void removeFirst() {
        points.pollFirst();
    }

    @Override
    public TrailSample peekFirst() {
        return points.peekFirst();
    }

    @Override
    public TrailSample peekLast() {
        return points.peekLast();
    }

    @Override
    public void pruneOlderThan(double minTime) {
        while (!points.isEmpty()) {
            var last = points.peekLast();

            if (last == null || last.time() >= minTime)
                break;

            points.removeLast();
        }
    }

    @Override
    public void trimToSize(int max) {
        while (points.size() > Math.max(0, max)) {
            points.removeLast();
        }
    }

    @Override
    public @NotNull Iterator<TrailSample> iterator() {
        return points.iterator();
    }
}
