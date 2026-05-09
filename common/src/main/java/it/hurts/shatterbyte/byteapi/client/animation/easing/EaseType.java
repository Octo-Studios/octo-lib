package it.hurts.shatterbyte.byteapi.client.animation.easing;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum EaseType {
    EASE_IN(Function::apply),
    EASE_OUT((f, d) -> 1 - f.apply(1 - d)),
    EASE_IN_OUT((f, d) -> {
        if (d < 0.5) {
            return 0.5 * f.apply(2 * d);
        } else {
            return 0.5 * (1 - f.apply(2 * (1 - d))) + 0.5;
        }
    });
//    EASE_OUT_IN((f, d) -> {
//        if (d >= 0.5) {
//            return 0.5 * f.apply(2 * d);
//        } else {
//            return 0.5 * (1 - f.apply(2 * (1 - d))) + 0.5;
//        }
//    });

    private final BiFunction<Function<Double, Double>, Double, Double> function;
    EaseType(BiFunction<Function<Double, Double>, Double, Double> function) {
        this.function = function;
    }

    public double apply(Function<Double, Double> function, double t) {
        return this.function.apply(function, t);
    }
}
