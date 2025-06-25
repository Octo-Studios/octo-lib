package it.hurts.octostudios.octolib.client.animation;

import net.minecraft.client.Minecraft;

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

        Minecraft.getInstance().submit(runnable);
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
