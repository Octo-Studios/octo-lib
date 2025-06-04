package it.hurts.octostudios.octolib.client.animator;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AnimatorSystem {
    private static final Queue<Animator> ACTIVE_ANIMATORS = new ConcurrentLinkedQueue<>();

    public static Animator addAnimator(Animator animator) {
        if (!RenderSystem.isOnRenderThread()) {
            throw new IllegalStateException("AnimatorSystem must be called on render thread");
        }
        ACTIVE_ANIMATORS.add(animator);
        return animator;
    }

    public static void updateAll() {
        if (!RenderSystem.isOnRenderThread()) {
            throw new IllegalStateException("AnimatorSystem must be called on render thread");
        }

        Iterator<Animator> it = ACTIVE_ANIMATORS.iterator();
        while (it.hasNext()) {
            Animator anim = it.next();
            anim.update();
            if (anim.isFinished()) {
                it.remove();
            }
        }
    }

    public static void clear() {
        if (!RenderSystem.isOnRenderThread()) {
            throw new IllegalStateException("AnimatorSystem must be called on render thread");
        }
        ACTIVE_ANIMATORS.clear();
    }
}
