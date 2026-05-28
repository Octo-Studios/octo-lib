package it.hurts.shatterbyte.shatterlib.client.particle;

import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import net.minecraft.resources.Identifier;
import org.joml.Vector2f;

import java.util.Random;

public class GalacticUIParticle extends ExtendedUIParticle {
    private static final Random RANDOM = new Random();
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public GalacticUIParticle(float maxSpeed, int maxLifetime, float xStart, float yStart, Layer layer, float zOffset) {
        super(new Texture2D(Identifier.withDefaultNamespace("textures/particle/sga_").withSuffix(ALPHABET[RANDOM.nextInt(ALPHABET.length)] + ".png"),
                        0, 0, 8, 8, 5, 7),
                maxSpeed,
                maxLifetime,
                xStart,
                yStart,
                layer,
                zOffset
        );

        this.setSpeed(RANDOM.nextFloat() * 2 * maxSpeed);
        this.setAngularVelocity(this.getSpeed()*3);
        this.setColors(new ShatterColor(1f, 1f, 1f, 1f), new ShatterColor(0f, 1f, 1f, 0f));
        this.setDirection(new Vector2f(RANDOM.nextFloat() - 0.5f, RANDOM.nextFloat() - 0.5f).normalize());
        this.setRenderPipeline(UIParticle.ADDITIVE_PIPELINE);
        this.setGravity(0.01f);
        this.setGravityDirection(0f, 1f);
        this.setFriction(0.02f);
    }
}
