package it.hurts.shatterbyte.shatterlib.client.animation;

import lombok.Getter;
import lombok.Setter;

public abstract class Tweener {
    @Getter @Setter
    protected Tween tween;
    protected long startTimestampMillis;
    @Getter
    protected boolean finished;

    public void start() {
        startTimestampMillis = System.currentTimeMillis();
        finished = false;
    }

    public double getElapsedTime() {
        return (System.currentTimeMillis() - startTimestampMillis) / 1000d * tween.speedScale;
    }

    protected void finish() {
        finished = true;
    }

    public abstract boolean step();
}