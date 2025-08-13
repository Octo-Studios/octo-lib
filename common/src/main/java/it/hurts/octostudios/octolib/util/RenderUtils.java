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
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.awt.geom.Point2D;

public class RenderUtils {
    public static Vec2 toScreenCoords(Matrix4f matrix, double x, double y) {
        Matrix4f inverse = new Matrix4f(matrix);
        inverse.invert();
        return toViewportCoords(inverse, x, y);
    }

    public static Vec2 toViewportCoords(Matrix4f matrix, double x, double y) {
        Vector4f vec = new Vector4f((float) x, (float) y, 0.0f, 1.0f);
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

    public static void renderTextureFromCenter(PoseStack matrix, float centerX, float centerY, float width, float height, float scale, float zOffset) {
        renderTextureFromCenter(matrix, centerX, centerY, 0, 0, width, height, width, height, scale, zOffset);
    }

    public static void renderTextureFromCenter(PoseStack matrix, float centerX, float centerY, float texOffX, float texOffY, float texWidth, float texHeight, float width,
                                               float height, float scale, float zOffset) {
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        matrix.pushPose();

        matrix.translate(centerX, centerY, 0);
        matrix.scale(scale, scale, scale);

        Matrix4f m = matrix.last().pose();

        float u1 = texOffX / texWidth;
        float u2 = (texOffX + width) / texWidth;
        float v1 = texOffY / texHeight;
        float v2 = (texOffY + height) / texHeight;

        float w2 = width / 2F;
        float h2 = height / 2F;

        builder.addVertex(m, -w2, +h2, zOffset).setUv(u1, v2);
        builder.addVertex(m, +w2, +h2, zOffset).setUv(u2, v2);
        builder.addVertex(m, +w2, -h2, zOffset).setUv(u2, v1);
        builder.addVertex(m, -w2, -h2, zOffset).setUv(u1, v1);

        matrix.popPose();

        RenderPipelines.SOLID.getShaderDefines();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferUploader.drawWithShader(builder.buildOrThrow());
    }

    public static void renderTilingTexture(PoseStack matrix, float x, float y, float texOffX, float texOffY,
                                           float texWidth, float texHeight, float width, float height,
                                           float zOffset, boolean tileHorizontally, boolean tileVertically) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        int wrapS = tileHorizontally ? GL11.GL_REPEAT : GL11.GL_CLAMP;
        int wrapT = tileVertically ? GL11.GL_REPEAT : GL11.GL_CLAMP;
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapS);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapT);

        float uStart = texOffX / texWidth;
        float vStart = texOffY / texHeight;
        float uRange = tileHorizontally ? (width / texWidth) : 1.0f;
        float vRange = tileVertically ? (height / texHeight) : 1.0f;
        float uEnd = uStart + uRange;
        float vEnd = vStart + vRange;

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        matrix.pushPose();
        matrix.translate(x, y, 0);
        Matrix4f m = matrix.last().pose();

        builder.addVertex(m, 0f, height, zOffset).setUv(uStart, vEnd)
                .addVertex(m, width, height, zOffset).setUv(uEnd, vEnd)
                .addVertex(m, width, 0f, zOffset).setUv(uEnd, vStart)
                .addVertex(m, 0f, 0f, zOffset).setUv(uStart, vStart);

        matrix.popPose();
        BufferUploader.drawWithShader(builder.buildOrThrow());
    }
}
