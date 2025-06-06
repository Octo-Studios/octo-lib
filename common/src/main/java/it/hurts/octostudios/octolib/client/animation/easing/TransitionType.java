package it.hurts.octostudios.octolib.client.animation.easing;

import java.util.function.Function;

public enum TransitionType {
    LINEAR(t -> t),
    SINE(t -> 1 - Math.cos((t * Math.PI) / 2)),
    QUAD(t -> t * t),
    CUBIC(t -> t * t * t),
    QUART(t -> t * t * t * t),
    QUINT(t -> t * t * t * t * t),
    EXPO(t -> t == 0 ? 0 : Math.pow(2, 10 * t - 10)),
    CIRC(t -> 1 - Math.sqrt(1 - Math.pow(t, 2))),
    BACK(t -> {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return c3 * t * t * t - c1 * t * t;
    }),
    ELASTIC(t -> {
        double c4 = (2 * Math.PI) / 3;
        return t == 0 ? 0 : t == 1 ? 1
                : -Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * c4);
    }),
    BOUNCE(t -> 1 - applyEaseOutBounce(1 - t));

    private final Function<Double, Double> function;

    TransitionType(Function<Double, Double> function) {
        this.function = function;
    }

    public double apply(EaseType ease, double t) {
        return ease.apply(function, t);
    }

    public double runEquation(EaseType easeType, double time, double initial, double delta, double duration) {
        if (duration == 0) {
            // instant jump to final
            return initial + delta;
        }

        // normalize current time to [0, 1]
        double progress = Math.clamp(time / duration, 0, 1);

        // apply the transition/ease function to get interpolation factor
        double factor = this.apply(easeType, progress);

        // compute and return the interpolated value
        return initial + delta * factor;
    }

    private static double applyEaseOutBounce(double t) {
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
    }
}
