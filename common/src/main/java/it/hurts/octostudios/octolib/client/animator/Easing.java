package it.hurts.octostudios.octolib.client.animator;

import java.util.function.Function;

public enum Easing {
    LINEAR(t -> t),
    EASE_IN_SINE(t -> 1 - Math.cos((t * Math.PI) / 2)),
    EASE_OUT_SINE(t -> Math.sin((t * Math.PI) / 2)),
    EASE_IN_OUT_SINE(t -> -(Math.cos(Math.PI * t) - 1) / 2),
    EASE_IN_QUAD(t -> t * t),
    EASE_OUT_QUAD(t -> 1 - (1 - t) * (1 - t)),
    EASE_IN_OUT_QUAD(t -> t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2),
    EASE_IN_CUBIC(t -> t * t * t),
    EASE_OUT_CUBIC(t -> 1 - Math.pow(1 - t, 3)),
    EASE_IN_OUT_CUBIC(t -> t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2),
    EASE_IN_QUART(t -> t * t * t * t),
    EASE_OUT_QUART(t -> 1 - Math.pow(1 - t, 4)),
    EASE_IN_OUT_QUART(t -> t < 0.5 ? 8 * t * t * t * t : 1 - Math.pow(-2 * t + 2, 4) / 2),
    EASE_IN_QUINT(t -> t * t * t * t * t),
    EASE_OUT_QUINT(t -> 1 - Math.pow(1 - t, 5)),
    EASE_IN_OUT_QUINT(t -> t < 0.5 ? 16 * t * t * t * t * t : 1 - Math.pow(-2 * t + 2, 5) / 2),
    EASE_IN_EXPO(t -> t == 0 ? 0 : Math.pow(2, 10 * t - 10)),
    EASE_OUT_EXPO(t -> t == 1 ? 1 : 1 - Math.pow(2, -10 * t)),
    EASE_IN_OUT_EXPO(t -> {
        if (t == 0) return 0d;
        if (t == 1) return 1d;
        return t < 0.5
                ? Math.pow(2, 20 * t - 10) / 2
                : (2 - Math.pow(2, -20 * t + 10)) / 2;
    }),
    EASE_IN_CIRC(t -> 1 - Math.sqrt(1 - Math.pow(t, 2))),
    EASE_OUT_CIRC(t -> Math.sqrt(1 - Math.pow(t - 1, 2))),
    EASE_IN_OUT_CIRC(t -> t < 0.5
            ? (1 - Math.sqrt(1 - Math.pow(2 * t, 2))) / 2
            : (Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2),
    EASE_IN_BACK(t -> {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return c3 * t * t * t - c1 * t * t;
    }),
    EASE_OUT_BACK(t -> {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
    }),
    EASE_IN_OUT_BACK(t -> {
        double c1 = 1.70158;
        double c2 = c1 * 1.525;
        return t < 0.5
                ? (Math.pow(2 * t, 2) * ((c2 + 1) * 2 * t - c2)) / 2
                : (Math.pow(2 * t - 2, 2) * ((c2 + 1) * (t * 2 - 2) + c2) + 2) / 2;
    }),
    EASE_IN_ELASTIC(t -> {
        double c4 = (2 * Math.PI) / 3;
        return t == 0 ? 0 : t == 1 ? 1
                : -Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * c4);
    }),
    EASE_OUT_ELASTIC(t -> {
        double c4 = (2 * Math.PI) / 3;
        return t == 0 ? 0 : t == 1 ? 1
                : Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1;
    }),
    EASE_IN_OUT_ELASTIC(t -> {
        double c5 = (2 * Math.PI) / 4.5;
        if (t == 0) return 0d;
        if (t == 1) return 1d;
        double sin = Math.sin((20 * t - 11.125) * c5);
        return t < 0.5
                ? -(Math.pow(2, 20 * t - 10) * sin) / 2
                : (Math.pow(2, -20 * t + 10) * sin) / 2 + 1;
    }),
    EASE_OUT_BOUNCE(t -> {
        double n1 = 7.5625;
        double d1 = 2.75;
        if (t < 1 / d1) {
            return n1 * t * t;
        } else if (t < 2 / d1) {
            t -= 1.5 / d1;
            return n1 * t * t + 0.75;
        } else if (t < 2.5 / d1) {
            t -= 2.25 / d1;
            return n1 * t * t + 0.9375;
        } else {
            t -= 2.625 / d1;
            return n1 * t * t + 0.984375;
        }
    }),
    EASE_IN_BOUNCE(t -> 1 - EASE_OUT_BOUNCE.apply(1 - t)),
    EASE_IN_OUT_BOUNCE(t -> t < 0.5
            ? (1 - EASE_OUT_BOUNCE.apply(1 - 2 * t)) / 2
            : (1 + EASE_OUT_BOUNCE.apply(2 * t - 1)) / 2);

    private final Function<Double, Double> function;

    Easing(Function<Double, Double> function) {
        this.function = function;
    }

    public double apply(double t) {
        return function.apply(t);
    }

    public double flipped(double t) {
        return 1 - function.apply(1 - t);
    }
}

