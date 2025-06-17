package it.hurts.octostudios.octolib.client.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TweenSystem {
    private static final List<Tween> TWEENS = new ArrayList<>();
    private static final Queue<Tween> PENDING = new ConcurrentLinkedQueue<>();

    private static boolean running = false;

    public static void init() {
        if (running) return; // prevent multiple threads
        running = true;

        Thread tweenThread = new Thread(() -> {
            while (running) {
                Tween tween;
                while ((tween = PENDING.poll()) != null) {
                    if (TWEENS.contains(tween)) {
                        continue;
                    }

                    TWEENS.add(tween);
                }

                updateAll();

//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
            }
        }, "Tween thread");

        tweenThread.setDaemon(true); // dies when the app dies
        tweenThread.start();
    }

    protected static void addTween(Tween tween) {
        PENDING.add(tween);
    }

    public static void updateAll() {
        List<Tween> toRemove = new ArrayList<>();

        TWEENS.forEach(tween -> {
            if (!tween.step()) {
                tween.clear();
                toRemove.add(tween);
            }
        });
        TWEENS.removeAll(toRemove);
    }
}
