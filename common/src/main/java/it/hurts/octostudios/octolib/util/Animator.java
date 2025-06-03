package it.hurts.octostudios.octolib.util;

import lombok.Getter;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class Animator {
    private final Easing easing;
    private final double startValue;
    private final double endValue;
    private long startTimestamp;
    private long durationMillis;
    @Getter
    private boolean isFinished = false;

    private Consumer<Double> onUpdate;
    private Runnable onComplete;

    public Animator(Easing easing, double startValue, double endValue, double durationInSeconds, Consumer<Double> onUpdate, Runnable onComplete) {
        this.easing = easing;
        this.startValue = startValue;
        this.endValue = endValue;
        this.durationMillis = (long) (durationInSeconds * 1000);
        this.onUpdate = onUpdate;
        this.onComplete = onComplete;

        this.startTimestamp = System.currentTimeMillis();
    }

    public void update() {
        if (isFinished) {
            return;
        }

        long elapsedMillis = System.currentTimeMillis() - this.startTimestamp;
        double t = Math.min((double) elapsedMillis / durationMillis, 1.0);
        double easedT = easing.apply(t);
        double currentValue = Mth.lerp(easedT, startValue, endValue);

        if (onUpdate != null) {
            onUpdate.accept(currentValue);
        }

        if (t >= 1.0) {
            isFinished = true;
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }

    public void reset() {
        startTimestamp = System.currentTimeMillis();
        isFinished = false;
    }

    public void stop() {
        startTimestamp = System.currentTimeMillis();
        isFinished = true;
    }
}