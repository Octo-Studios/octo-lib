package it.hurts.octostudios.octolib.client.particle;

import it.hurts.octostudios.octolib.util.OctoColor;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.Random;

public class GalacticUIParticle extends UIParticle {
    private static final Random RANDOM = new Random();
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public GalacticUIParticle(float maxSpeed, int maxLifetime, float xStart, float yStart, Layer layer, float zOffset) {
        super(new Texture2D(ResourceLocation.withDefaultNamespace("textures/particle/sga_").withSuffix(ALPHABET[RANDOM.nextInt(ALPHABET.length)]+".png"),
                0, 0, 8, 8, 5, 7), maxSpeed, maxLifetime, xStart, yStart, layer, zOffset);

        this.angularVelocity = 120;
        this.speed = RANDOM.nextFloat()*2 * maxSpeed;
        this.endColor = new OctoColor(0f,1f,1f,0f);
        this.direction = new Vector2f(RANDOM.nextFloat()-0.5f, RANDOM.nextFloat()-0.5f).normalize();
        this.resizeWithLifetime = false;
        this.size = 1f;
    }
}