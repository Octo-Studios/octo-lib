package it.hurts.octostudios.octolib.client.animation;

public class RunnableTweener extends Tweener {
    private final Runnable runnable;
    private double delay;

    @Override
    public boolean step(double dt) {
        if (finished) {
            return false;
        }

        elapsedTime += dt;

        if (elapsedTime < delay) {
            return true;
        }

        runnable.run();
        finish();
        return false;
    }

    public RunnableTweener setDelay(double delayInSeconds) {
        this.delay = delayInSeconds;
        return this;
    }

    public RunnableTweener(Runnable runnable) {
        this.runnable = runnable;
    }
}
