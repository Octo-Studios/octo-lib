package it.hurts.octostudios.octolib;

import it.hurts.octostudios.octolib.util.Animator;

import java.util.HashMap;
import java.util.Map;

public class AnimatorSystem {
    private static final Map<String, Animator> animatorMap = new HashMap<>();

    public static void addAnimator(String id, Animator animator) {
        animatorMap.put(id, animator);
    }

    public static void removeAnimator(String id) {
        animatorMap.remove(id);
    }

    public static void updateAll() {
        for (Animator animator : animatorMap.values()) {
            animator.update();
        }
        animatorMap.values().removeIf(Animator::isFinished);
    }

    public static boolean hasAnimator(String id) {
        return animatorMap.containsKey(id);
    }

    public static void clear() {
        animatorMap.clear();
    }
}
