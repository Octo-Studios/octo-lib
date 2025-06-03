package it.hurts.octostudios.octolib;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.util.internal.ConcurrentSet;
import it.hurts.octostudios.octolib.util.Animator;

import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AnimatorSystem {
    private static final Queue<Animator> ANIMATOR_SET = new ConcurrentLinkedQueue<>();

    public static Animator addAnimator(Animator animator) {
        assertThread();
        ANIMATOR_SET.add(animator);
        return animator;
    }

    public static void updateAll() {
        assertThread();
        for (Animator animator : ANIMATOR_SET) {
            animator.update();
        }
        ANIMATOR_SET.removeIf(Animator::isFinished);
    }

    public static void clear() {
        assertThread();
        ANIMATOR_SET.clear();
    }

    private static void assertThread() {
        if (!RenderSystem.isOnRenderThread()) {
            throw new IllegalStateException("AnimatorSystem called from the wrong thread");
        }
    }
}
