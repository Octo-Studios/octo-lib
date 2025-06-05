package it.hurts.octostudios.octolib.client.animation;

@FunctionalInterface
public interface Interpolator<T> {
    T lerp(T from, T to, double t);
}