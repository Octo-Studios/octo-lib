package it.hurts.octostudios.octolib.client.screen.widget;

import org.joml.Matrix4f;

public interface HasRenderMatrix {
    Matrix4f getMatrix();
    void setMatrix(Matrix4f matrix);
}
