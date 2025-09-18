package it.hurts.shatterbyte.shatterlib.client.shake;

import org.joml.Vector2f;

public interface Shakeable {
    void setShakeOffset(Vector2f offset);
    Vector2f getShakeOffset();
}
