package it.hurts.octostudios.octolib.client.screen.widget;

import org.joml.Matrix4f;

public interface HasRenderMatrix {
    static Matrix4f getFinalMatrix(HasRenderMatrix object) {
        if (object instanceof Child<?> child && child.getParent() != null && child.getParent() instanceof HasRenderMatrix parent) {
            Matrix4f parentMatrix = getFinalMatrix(parent);

            return new Matrix4f(parentMatrix).mul(object.getMatrix());
        }

        return new Matrix4f(object.getMatrix());
    }

    Matrix4f getMatrix();

    void setMatrix(Matrix4f matrix);
}
