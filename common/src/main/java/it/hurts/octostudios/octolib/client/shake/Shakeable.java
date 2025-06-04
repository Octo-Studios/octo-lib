package it.hurts.octostudios.octolib.client.shake;

import org.joml.Vector2f;

public interface Shakeable {
    void setShakeOffset(Vector2f offset);
    Vector2f getShakeOffset();
}
