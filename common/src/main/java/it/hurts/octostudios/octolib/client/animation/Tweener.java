package it.hurts.octostudios.octolib.client.animation;

import lombok.Getter;
import lombok.Setter;

public abstract class Tweener {
    @Getter @Setter
    protected Tween tween;
    protected double elapsedTime;
    @Getter
    protected boolean finished;

    public void start() {
        elapsedTime = 0;
        finished = false;
    }

    protected void finish() {
        finished = true;
    }

    public abstract boolean step(double dt);
}