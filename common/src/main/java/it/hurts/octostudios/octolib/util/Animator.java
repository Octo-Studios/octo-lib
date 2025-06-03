package it.hurts.octostudios.octolib.util;

import it.hurts.octostudios.octolib.AnimatorSystem;
import lombok.Getter;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class Animator {
    private Easing easing;
    private double startValue;
    private double endValue;
    private long startTimestamp;
    private long durationMillis;

    @Getter
    private boolean isFinished = false;

    @Getter
    private boolean isRunning = false;

    private Consumer<Double> onUpdate;
    private Runnable onComplete;

    @Getter
    private Animator nextAnimator;
    @Getter
    private Animator prevAnimator;

    public Animator(Easing easing, double startValue, double endValue, double durationInSeconds, Consumer<Double> onUpdate, Runnable onComplete) {
        this.easing = easing;
        this.startValue = startValue;
        this.endValue = endValue;
        this.durationMillis = (long) (durationInSeconds * 1000);
        this.onUpdate = onUpdate;
        this.onComplete = onComplete;
    }

    public Animator(Easing easing, double startValue, double endValue, double durationInSeconds, Consumer<Double> onUpdate) {
        this(easing, startValue, endValue, durationInSeconds, onUpdate, () -> {});
    }

    public void update() {
        if (isFinished) {
            return;
        }

        long elapsedMillis = System.currentTimeMillis() - this.startTimestamp;
        double t = Math.min((double) elapsedMillis / durationMillis, 1.0);
        this.isRunning = true;

        if (onUpdate != null && startValue != endValue) {
            double easedT = easing.apply(t);
            double currentValue = Mth.lerp(easedT, startValue, endValue);
            onUpdate.accept(currentValue);
        }

        if (t >= 1.0) {
            if (onComplete != null) {
                onComplete.run();
            }

            if (this.nextAnimator == null) {
                this.stop();
            } else {
                this.switchToNext();
            }
        }
    }

    public Animator start() {
        Animator nextTest = this;
        while (true) {
            if (nextTest.getPrevAnimator() == null) {
                nextTest.startTimestamp = System.currentTimeMillis();
                nextTest.isFinished = false;
                AnimatorSystem.addAnimator(nextTest);
                return nextTest;
            }

            nextTest = nextTest.getPrevAnimator();
        }
    }

    public void stop() {
        isFinished = true;
        this.isRunning = false;
    }

    public Animator then(Animator nextAnimator) {
        nextAnimator.prevAnimator = this;
        this.nextAnimator = nextAnimator;
        return this.nextAnimator;
    }

    public Animator sleep(double durationInSeconds) {
        return this.then(new Animator(Easing.LINEAR, 0, 0, durationInSeconds, d -> {}, () -> {}));
    }

    public Animator callback(Runnable callback) {
        return this.then(new Animator(Easing.LINEAR, 0, 0, 0, d -> {}, callback));
    }

    private void switchToNext() {
        this.easing = this.nextAnimator.easing;
        this.startValue = this.nextAnimator.startValue;
        this.endValue = this.nextAnimator.endValue;
        this.startTimestamp = System.currentTimeMillis();
        this.durationMillis = this.nextAnimator.durationMillis;
        this.isFinished = this.nextAnimator.isFinished;
        this.onUpdate = this.nextAnimator.onUpdate;
        this.onComplete = this.nextAnimator.onComplete;
        this.nextAnimator = this.nextAnimator.nextAnimator;
    }
}