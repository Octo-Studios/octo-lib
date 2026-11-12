package it.hurts.octostudios.octolib.client.shake;

import org.joml.Vector2f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShakeSystem {
    private static final Map<Shakeable, ShakeData> ACTIVE_SHAKES = new ConcurrentHashMap<>();

    public static void startShake(Shakeable target, ShakeData data) {
        ACTIVE_SHAKES.put(target, data);
    }

    public static void stopShake(Shakeable target) {
        ACTIVE_SHAKES.remove(target);
        // reset offset so it doesn’t stay offset after removal
        target.setShakeOffset(new Vector2f(0, 0));
    }

    public static void updateAll() {
        for (Map.Entry<Shakeable, ShakeData> entry : ACTIVE_SHAKES.entrySet()) {
            Shakeable target = entry.getKey();
            ShakeData data = entry.getValue();
            Vector2f offset = data.update();
            target.setShakeOffset(offset);
        }

        ACTIVE_SHAKES.values().removeIf(ShakeData::isFinished);
    }

    public static void clearAll() {
        for (Shakeable target : ACTIVE_SHAKES.keySet()) {
            ShakeSystem.stopShake(target);
        }
    }
}