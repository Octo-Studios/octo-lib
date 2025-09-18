package it.hurts.shatterbyte.shatterlib.client.screen.widget;

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

public interface HasRenderMatrix {
    Matrix3x2f getMatrix();
    void setMatrix(Matrix3x2f matrix);
}
