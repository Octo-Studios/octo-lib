package it.hurts.shatterbyte.byteapi.util;

import lombok.Getter;
import net.minecraft.util.Util;

public class DeltaTimeTracker {
    private static long lastTimeNanos = -1L;
    @Getter
    private static double deltaSeconds = 0.0;

    public static void updateDeltaTime() {
        long now = Util.getNanos();
        if (lastTimeNanos != -1L) {
            deltaSeconds = (now - lastTimeNanos) / 1_000_000_000.0;
        }

        lastTimeNanos = now;
    }
}
