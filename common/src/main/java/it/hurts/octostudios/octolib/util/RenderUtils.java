package it.hurts.octostudios.octolib.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public class RenderUtils {
    public static void renderTextureFromCenter(PoseStack matrix, float centerX, float centerY, float width, float height, float scale, float zOffset) {
        renderTextureFromCenter(matrix, centerX, centerY, 0, 0, width, height, width, height, scale, zOffset);
    }

    public static void renderTextureFromCenter(PoseStack matrix, float centerX, float centerY, float texOffX, float texOffY, float texWidth, float texHeight, float width,
                                               float height, float scale, float zOffset) {
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

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

        BufferUploader.drawWithShader(builder.buildOrThrow());
    }
}
