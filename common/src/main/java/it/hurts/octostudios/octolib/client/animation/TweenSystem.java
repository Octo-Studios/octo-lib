package it.hurts.octostudios.octolib.client.animation;

import java.util.ArrayList;
import java.util.List;

public class TweenSystem {
    private static final List<Tween> TWEENS = new ArrayList<>();

    protected static void addTween(Tween tween) {
        TWEENS.add(tween);
    }

    public static void updateAll(double dt) {
        List<Tween> toRemove = new ArrayList<>();

        TWEENS.forEach(tween -> {
            if (!tween.step(dt)) {
                tween.clear();
                toRemove.add(tween);
            }
        });
        TWEENS.removeAll(toRemove);
    }
}
