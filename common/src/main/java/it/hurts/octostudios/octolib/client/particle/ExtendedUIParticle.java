package it.hurts.octostudios.octolib.client.particle;

import it.hurts.octostudios.octolib.util.VectorUtils;
import net.minecraft.util.Mth;
import org.joml.Vector2f;

public class ExtendedUIParticle extends UIParticle {
    public Vector2f gravityDirection = new Vector2f(0, 1);
    public float gravity = 0;
    private float gravityAccel = 0;
    public float friction = 0;
    public float angularVelocity = 0;

    public ExtendedUIParticle(Texture2D texture, float maxSpeed, int lifetime, float xStart, float yStart, Layer layer, float zOffset) {
        super(texture, maxSpeed, lifetime, xStart, yStart, layer, zOffset);
    }

    @Override
    public void tick() {
        if (this.isExpired()) return;

        super.tick();

        // Apply angular rotation
        if (angularVelocity != 0) {
            //this.transform.setRotation(this.transform.getRotation() + angularVelocity);
            this.direction = VectorUtils.rotate(this.direction.normalize(), angularVelocity);
        } else {
            this.direction.normalize();
        }

        // Apply friction
        this.speed = Mth.clamp(this.speed * (1 - friction), 0, this.getMaxSpeed());

        // Apply gravity
        gravityAccel += gravity;
        if (gravityAccel != 0) {
            this.transform.getPosition().add(gravityDirection.mul(gravityAccel));
        }
    }
}

