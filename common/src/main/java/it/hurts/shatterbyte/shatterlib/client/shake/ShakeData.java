package it.hurts.shatterbyte.shatterlib.client.shake;

import it.hurts.shatterbyte.shatterlib.module.config.annotation.SimpleProp;
import it.hurts.shatterbyte.shatterlib.module.config.type.BooleanEntry;
import lombok.Getter;
import lombok.ToString;
import org.joml.Vector2f;

import java.util.Random;

@ToString
public class ShakeData {
    @SimpleProp
    private final Vector2f amplitude;
    private final Vector2f frequency;

    private long durationMillis;
    private long startTimestamp;
    private short seed;

    @Getter
    private BooleanEntry isFinished = BooleanEntry.builder(false)
            .withComment("True if the shake is finished.")
            .build();

    public ShakeData(Vector2f amplitude, Vector2f frequency, double durationInSeconds, short seed) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.startTimestamp = System.currentTimeMillis();
        this.durationMillis = (long) (durationInSeconds * 1000);

        this.seed = seed;
    }

    public ShakeData(Vector2f amplitude, Vector2f frequency, double durationInSeconds) {
        this(amplitude, frequency, durationInSeconds, (short) new Random().nextInt(Short.MIN_VALUE, Short.MAX_VALUE));
    }

    public ShakeData(float amplitude, float frequency, double durationInSeconds) {
        this(new Vector2f(amplitude, amplitude), new Vector2f(frequency*1.24f, frequency*0.81f), durationInSeconds);
    }

    /**
     * call this once per frame. it returns a Vector2f containing the new x/y offset.
     */
    public Vector2f update() {
        if (this.isFinished.getValue()) {
            return new Vector2f(0, 0);
        }

        long now = System.currentTimeMillis();

        long elapsedTime = (now - startTimestamp);
        float t = (float) Math.min((double) elapsedTime / durationMillis, 1.0);
        float easedT = 1 - t;

        if (t >= 1f) {
            this.isFinished.setValue(true);
        }

        // compute offset using sine waves
        float offsetX = amplitude.x() * (float) Math.sin(seed * Math.PI + elapsedTime/1000f * frequency.x() * 2 * Math.PI) * easedT;
        float offsetY = amplitude.y() * (float) Math.sin(seed * Math.PI + elapsedTime/1000f * frequency.y() * 2 * Math.PI) * easedT;

        return new Vector2f(offsetX, offsetY);
    }
}
