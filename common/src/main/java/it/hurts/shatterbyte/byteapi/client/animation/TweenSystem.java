package it.hurts.shatterbyte.byteapi.client.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

public class TweenSystem {
    private static final List<Tween> TWEENS = new ArrayList<>();
    private static final Queue<Tween> PENDING = new ConcurrentLinkedQueue<>();
    private static final long UPDATE_INTERVAL_NANOS = 3_000_000L;

    private static boolean running = false;

    public static void init() {
        if (running) return;
        running = true;

        Thread tweenThread = new Thread(() -> {
            while (running) {
                if (TWEENS.isEmpty() && PENDING.isEmpty()) {
                    LockSupport.parkNanos(UPDATE_INTERVAL_NANOS);
                    continue;
                }

                Tween tween;
                while ((tween = PENDING.poll()) != null) {
                    if (!TWEENS.contains(tween)) {
                        TWEENS.add(tween);
                    }
                }

                updateAll();

                LockSupport.parkNanos(UPDATE_INTERVAL_NANOS);
            }
        }, "Tween thread");

        tweenThread.setDaemon(true);
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

    public static class RenderThreadExecutor {
        private static final Queue<Runnable> renderQueue = new ConcurrentLinkedQueue<>();

        public static void runOnRenderThread(Runnable task) {
            renderQueue.add(task);
        }

        public static void executeAll() {
            Runnable task;
            while ((task = renderQueue.poll()) != null) {
                task.run();
            }
        }
    }

    public static class ServerThreadExecutor {
        private static final Queue<Runnable> serverQueue = new ConcurrentLinkedQueue<>();

        public static void runOnServerThread(Runnable task) {
            serverQueue.add(task);
        }

        public static void executeAll() {
            Runnable task;
            while ((task = serverQueue.poll()) != null) {
                task.run();
            }
        }
    }
}
