package it.hurts.octostudios.octolib.client.particle;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Mth;
import org.joml.Vector2f;

@Getter
public class Transform {
    @Setter private Vector2f position;
    private float roll;
    @Setter private Vector2f size;

    private Vector2f oldPosition;
    private float oldRotation;
    private Vector2f oldSize;

    public Transform(Vector2f position, float roll, Vector2f size) {
        this.position = position;
        this.roll = roll;
        this.size = size;

        this.oldPosition = new Vector2f(position);
        this.oldRotation = roll;
        this.oldSize = new Vector2f(size);
    }

    public void setRoll(float angle) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        this.roll = angle;
    }

    public void updateOldValues() {
        this.oldPosition = new Vector2f(position);
        this.oldRotation = roll;
        this.oldSize = new Vector2f(size);
    }

    public Vector2f getInterpolatedPosition(float partialTicks) {
        return new Vector2f(
                Mth.lerp(partialTicks, oldPosition.x, position.x),
                Mth.lerp(partialTicks, oldPosition.y, position.y)
        );
    }

    public float getInterpolatedRoll(float partialTicks) {
        return Mth.lerp(partialTicks, oldRotation, roll);
    }

    public Vector2f getInterpolatedSize(float partialTicks) {
        return new Vector2f(
                Mth.lerp(partialTicks, oldSize.x, size.x),
                Mth.lerp(partialTicks, oldSize.y, size.y)
        );
    }
}
