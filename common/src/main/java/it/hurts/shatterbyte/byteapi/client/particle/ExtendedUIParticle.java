package it.hurts.shatterbyte.byteapi.client.particle;

import it.hurts.shatterbyte.byteapi.util.VectorUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Mth;
import org.joml.Vector2f;

public class ExtendedUIParticle extends UIParticle {
    @Getter private Vector2f gravityDirection = new Vector2f(0, 1);
    @Getter @Setter private float gravity = 0;
    @Getter @Setter private float friction = 0;
    @Getter @Setter private float angularVelocity = 0;
    private float gravityAccel = 0;

    public ExtendedUIParticle(Texture2D texture, float maxSpeed, int lifetime, float xStart, float yStart, Layer layer, float zOffset) {
        super(texture, maxSpeed, lifetime, xStart, yStart, layer, zOffset);
    }

    public void setGravityDirection(float x, float y) {
        this.gravityDirection = new Vector2f(x, y).normalize();
    }

    public void setGravityDirection(Vector2f gravityDirection) {
        this.gravityDirection = new Vector2f(gravityDirection).normalize();
    }

    @Override
    public void tick() {
        if (this.isExpired()) return;

        super.tick();

        // Apply angular rotation
        if (angularVelocity != 0) {
            //this.transform.setRotation(this.transform.getRotation() + angularVelocity);
            this.setDirection(VectorUtils.rotate(this.getDirection().normalize(), angularVelocity));
        } else {
            this.getDirection().normalize();
        }

        // Apply friction
        this.setSpeed(Mth.clamp(this.getSpeed() * (1 - friction), 0, this.getMaxSpeed()));

        // Apply gravity
        gravityAccel += gravity;
        if (gravityAccel != 0) {
            this.getTransform().getPosition().add(new Vector2f(gravityDirection).mul(gravityAccel));
        }
    }
}

