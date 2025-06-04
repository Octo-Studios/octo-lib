package it.hurts.octostudios.octolib.client.animator;

import lombok.Getter;

import java.util.function.Consumer;

public class Animator {
    private Easing easing;
    private double startValue;
    private double endValue;
    private long durationMillis;
    private long startTimestamp;
    @Getter
    private boolean isFinished = false;
    @Getter
    private boolean isRunning = false;
    private Consumer<Double> onUpdate;
    private Runnable onComplete;
    private Animator nextAnimator;
    private Animator prevAnimator;

    public Animator(Easing easing, double startValue, double endValue, double durationSeconds,
                    Consumer<Double> onUpdate, Runnable onComplete) {
        this.easing = easing;
        this.startValue = startValue;
        this.endValue = endValue;
        this.durationMillis = (long) (durationSeconds * 1000);
        this.onUpdate = onUpdate != null ? onUpdate : v -> {};
        this.onComplete = onComplete != null ? onComplete : () -> {};
    }

    public Animator(Easing easing, double startValue, double endValue,
                    double durationSeconds, Consumer<Double> onUpdate) {
        this(easing, startValue, endValue, durationSeconds, onUpdate, null);
    }

    public Animator then(Animator next) {
        next.prevAnimator = this;
        this.nextAnimator = next;
        return next;
    }

    public Animator sleep(double seconds) {
        return then(new Animator(Easing.LINEAR, 0, 0, seconds, v -> {}, () -> {}));
    }

    public Animator callback(Runnable callback) {
        return then(new Animator(Easing.LINEAR, 0, 0, 0, v -> {}, callback));
    }

    public Animator start() {
        Animator root = this;
        while (root.prevAnimator != null) {
            root = root.prevAnimator;
        }

        if (root.durationMillis == 0) {
            root.onComplete.run();
            if (root.nextAnimator != null) {
                root.nextAnimator.start();
            }
        } else {
            root.startTimestamp = System.currentTimeMillis();
            root.isFinished = false;
            root.isRunning = true;
            AnimatorSystem.addAnimator(root);
        }

        return this;
    }


    public void update() {
        if (isFinished || !isRunning) return;

        long elapsed = System.currentTimeMillis() - startTimestamp;
        double t = Math.min((double) elapsed / durationMillis, 1.0);
        double easedT = easing.apply(t);
        double current = startValue + (endValue - startValue) * easedT;
        onUpdate.accept(current);

        if (t >= 1.0) {
            finish();
        }
    }

    private void finish() {
        isFinished = true;
        isRunning = false;
        onComplete.run();
        if (nextAnimator != null) {
            nextAnimator.startTimestamp = System.currentTimeMillis();
            nextAnimator.isFinished = false;
            nextAnimator.isRunning = true;
            AnimatorSystem.addAnimator(nextAnimator);
        }
        // break the link to help GC
        if (nextAnimator != null) {
            nextAnimator.prevAnimator = null;
            this.nextAnimator = null;
        }
    }

    public void stop() {
        isFinished = true;
        isRunning = false;

        if (prevAnimator != null) {
            prevAnimator.stop();
        }
    }

    public void reset() {
        onUpdate.accept(startValue);

        if (prevAnimator != null) {
            prevAnimator.reset();
        }
    }

    public void pause() {
        isRunning = false;

        if (prevAnimator != null) {
            prevAnimator.pause();
        }
    }

    public void resume() {
        if (!isFinished && !isRunning) {
            long elapsed = System.currentTimeMillis() - startTimestamp;
            startTimestamp = System.currentTimeMillis() - elapsed;
            isRunning = true;
        }

        if (prevAnimator != null) {
            prevAnimator.resume();
        }
    }

}
