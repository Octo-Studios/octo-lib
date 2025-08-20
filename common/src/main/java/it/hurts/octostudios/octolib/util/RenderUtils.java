package it.hurts.octostudios.octolib.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.joml.*;
import org.lwjgl.opengl.GL11;

import java.awt.geom.Point2D;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderPipelines.GUI;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;

public class RenderUtils {
    public static final Function<ResourceLocation, RenderType> OCTO_GUI = rl -> RenderType.create("octogui", 1536, GUI_TEXTURED,
            RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(rl, false))
                    .createCompositeState(false));

    public static Vec2 toScreenCoords(Matrix3x2f matrix, double x, double y) {
        Matrix3x2f inverse = new Matrix3x2f(matrix);
        inverse.invert();
        return toViewportCoords(inverse, x, y);
    }

    public static Vec2 toViewportCoords(Matrix3x2f matrix, double x, double y) {
        Vector3f vec = new Vector3f((float) x, (float) y, 1.0f);
        vec = matrix.transform(vec);
        return new Vec2(vec.x(), vec.y());
    }

    private boolean isPointInQuad(double x, double y, Point2D[] quad) {
        // we divide the quad into two triangles and check if the point is in either one
        return pointInTriangle(x, y, quad[0], quad[1], quad[2]) ||
                pointInTriangle(x, y, quad[0], quad[2], quad[3]);
    }

    private boolean pointInTriangle(double px, double py, Point2D a, Point2D b, Point2D c) {
        double area = 0.5 * (-b.getY() * c.getX() + a.getY() * (-b.getX() + c.getX()) +
                a.getX() * (b.getY() - c.getY()) + b.getX() * c.getY());

        double sign = area < 0 ? -1 : 1;

        double s = (a.getY() * c.getX() - a.getX() * c.getY() + (c.getY() - a.getY()) * px + (a.getX() - c.getX()) * py) * sign;
        double t = (a.getX() * b.getY() - a.getY() * b.getX() + (a.getY() - b.getY()) * px + (b.getX() - a.getX()) * py) * sign;

        return s >= 0 && t >= 0 && (s + t) <= 2 * area * sign;
    }

    public static void renderTextureFromCenter(MultiBufferSource.BufferSource bufferSource, ResourceLocation texture, Matrix3x2fStack matrix, float centerX, float centerY, float width, float height, float scale, int color, float zOffset) {
        renderTextureFromCenter(bufferSource, texture, matrix, centerX, centerY, 0, 0, width, height, width, height, scale, color, zOffset);
    }

    public static void renderTextureFromCenter(MultiBufferSource.BufferSource bufferSource, ResourceLocation texture, Matrix3x2fStack matrix, float centerX, float centerY, float texOffX, float texOffY, float texWidth, float texHeight, float width,
                                               float height, float scale, int color, float zOffset) {
        VertexConsumer builder = bufferSource.getBuffer(OCTO_GUI.apply(texture));

        matrix.pushMatrix();

        matrix.translate(centerX, centerY);
        matrix.scale(scale, scale);

        Matrix3x2f m = matrix;

        float u1 = texOffX / texWidth;
        float u2 = (texOffX + width) / texWidth;
        float v1 = texOffY / texHeight;
        float v2 = (texOffY + height) / texHeight;

        float w2 = width / 2F;
        float h2 = height / 2F;

        builder.addVertexWith2DPose(m, -w2, +h2, zOffset).setUv(u1, v2).setColor(color);
        builder.addVertexWith2DPose(m, +w2, +h2, zOffset).setUv(u2, v2).setColor(color);
        builder.addVertexWith2DPose(m, +w2, -h2, zOffset).setUv(u2, v1).setColor(color);
        builder.addVertexWith2DPose(m, -w2, -h2, zOffset).setUv(u1, v1).setColor(color);

        matrix.popMatrix();
    }

    public static void renderTilingTexture(MultiBufferSource.BufferSource bufferSource, ResourceLocation texture, Matrix3x2fStack matrix, float x, float y, float texOffX, float texOffY,
                                           float texWidth, float texHeight, float width, float height,
                                           int color, float zOffset, boolean tileHorizontally, boolean tileVertically) {
        float uStart = texOffX / texWidth;
        float vStart = texOffY / texHeight;
        float uRange = tileHorizontally ? (width / texWidth) : 1.0f;
        float vRange = tileVertically ? (height / texHeight) : 1.0f;
        float uEnd = uStart + uRange;
        float vEnd = vStart + vRange;

        VertexConsumer builder = bufferSource.getBuffer(OCTO_GUI.apply(texture));

        matrix.pushMatrix();
        matrix.translate(x, y);

        builder.addVertexWith2DPose(matrix, 0f, height, zOffset).setUv(uStart, vEnd).setColor(color)
                .addVertexWith2DPose(matrix, width, height, zOffset).setUv(uEnd, vEnd).setColor(color)
                .addVertexWith2DPose(matrix, width, 0f, zOffset).setUv(uEnd, vStart).setColor(color)
                .addVertexWith2DPose(matrix, 0f, 0f, zOffset).setUv(uStart, vStart).setColor(color);

        matrix.popMatrix();
    }
}
