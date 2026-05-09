package it.hurts.shatterbyte.byteapi.client.animation;

public class RunnableTweener extends Tweener {
    private final Runnable runnable;
    private double delay;

    @Override
    public boolean step() {
        if (finished) {
            return false;
        }

        if (getElapsedTime() < delay) {
            return true;
        }

        TweenSystem.RenderThreadExecutor.runOnRenderThread(runnable);
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
