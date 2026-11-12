package it.hurts.octostudios.octolib.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import it.hurts.octostudios.octolib.mixin.GuiGraphicsAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.joml.*;

import java.awt.geom.Point2D;
import java.lang.Math;
import java.util.function.BiFunction;

public class RenderUtils {
    public static final BiFunction<RenderPipeline, ResourceLocation, RenderType> OCTO_GUI = (pipeline, rl) -> RenderType.create("octogui", 1536, pipeline,
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

    public static void renderTextureFromCenter(RenderPipeline pipeline, ResourceLocation texture, GuiGraphics guiGraphics, float centerX, float centerY, float width, float height, float scale, int color, float zOffset) {
        renderTextureFromCenter(pipeline, texture, guiGraphics, centerX, centerY, 0f, 0f, (int) width, (int) height, width, height, scale, color);
    }

    public static void renderTextureFromCenter(
            RenderPipeline pipeline,
            ResourceLocation texture,
            GuiGraphics guiGraphics,
            float centerX,
            float centerY,
            float texOffX,
            float texOffY,
            int texWidth,                // changed to int as requested
            int texHeight,               // changed to int as requested
            float width,
            float height,
            float scale,
            int color
    ) {
        // compute scaled width/height (kept as floats)
        float scaledWidth = width * scale;
        float scaledHeight = height * scale;

        // top-left position so the texture is rendered centered at (centerX, centerY)
        float x = centerX - scaledWidth * 0.5f;
        float y = centerY - scaledHeight * 0.5f;

        // texture coordinates in texels (keep as floats to preserve precision)
        float u = texOffX;
        float v = texOffY;

        // region size in texels (what portion of the texture atlas to draw)
        int regionW = Math.round(width);   // original region width/height (unscaled)
        int regionH = Math.round(height);

        guiGraphics.blit(pipeline, texture, (int) x, (int) y, u, v, regionW, regionH, texWidth, texHeight, color);
    }

    public static void renderTilingTexture(RenderPipeline pipeline, ResourceLocation texture, GuiGraphics guiGraphics, float x, float y, float texOffX, float texOffY,
                                           float texWidth, float texHeight, float width, float height,
                                           int color, boolean tileHorizontally, boolean tileVertically) {
        float uStart = texOffX / texWidth;
        float vStart = texOffY / texHeight;
        float uRange = tileHorizontally ? (width / texWidth) : 1.0f;
        float vRange = tileVertically ? (height / texHeight) : 1.0f;
        float uEnd = uStart + uRange;
        float vEnd = vStart + vRange;

        Matrix3x2fStack matrix = guiGraphics.pose();
        matrix.pushMatrix();
        matrix.translate(x, y);

//        builder.addVertex(0f, height*16, 100).setUv(uStart, vEnd).setColor(color)
//                .addVertex(width, height*16, 100).setUv(uEnd, vEnd).setColor(color)
//                .addVertex(width, 0f, 100).setUv(uEnd, vStart).setColor(color)
//                .addVertex(0f, 0f, 100).setUv(uStart, vStart).setColor(color);

        GuiGraphicsAccessor accessor = (GuiGraphicsAccessor) guiGraphics;
        accessor.innerBlitAccessor(pipeline, texture, 0, (int) width, 0, (int) height, uStart, uEnd, vStart, vEnd, color);

        //guiGraphics.renderOutline(0,0, (int) width, (int) height,color);
        matrix.popMatrix();


        //bufferSource.endBatch();
    }
}
