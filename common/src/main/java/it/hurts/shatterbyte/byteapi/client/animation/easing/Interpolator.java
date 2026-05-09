package it.hurts.shatterbyte.byteapi.client.animation.easing;

@FunctionalInterface
public interface Interpolator<T> {
    T lerp(T from, T to, double t);
}