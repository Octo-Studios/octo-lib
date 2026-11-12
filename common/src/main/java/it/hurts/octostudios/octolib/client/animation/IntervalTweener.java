package it.hurts.octostudios.octolib.client.animation;

public class IntervalTweener extends Tweener {
    private final double duration;

    @Override
    public boolean step() {
        if (finished) {
            return false;
        }

        if (getElapsedTime() < duration) {
            return true;
        }

        finish();
        return false;
    }

    public IntervalTweener(double duration) {
        this.duration = duration;
    }
}
