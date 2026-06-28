package it.hurts.shatterbyte.shatterlib.util;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Util;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.Color;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderPipelines.*;

public class TesselatorUtils {

    public static final RenderPipeline TRAIL_PIPELINE = RenderPipeline.builder(ENTITY_SNIPPET)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("PER_FACE_LIGHTING")
            .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
            .withPolygonMode(PolygonMode.FILL)
            .withVertexBinding(0, DefaultVertexFormat.ENTITY)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withLocation(Identifier.fromNamespaceAndPath(ShatterLib.MOD_ID, "trail"))
            .build();

    private static final Function<Identifier, RenderType> RENDER_TYPE_FUNCTION = Util.memoize(
            (identifier) -> {
                RenderSetup renderSetup = RenderSetup.builder(TRAIL_PIPELINE)
                        .withTexture("Sampler0", identifier)
                        .useLightmap()
                        .useOverlay()
                        .affectsCrumbling()
                        .sortOnUpload()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup();
                return RenderType.create("entity_translucent_cull", renderSetup);
            }
    );

    public static final RenderType TRAIL = RENDER_TYPE_FUNCTION.apply(Identifier.fromNamespaceAndPath(ShatterLib.MOD_ID, "textures/trail.png"));

    public static void drawFullQuadWithColor(VertexConsumer tes, Matrix4f matrix4f, float pos1X, float pos1Y, float pos1Z, float pos2X,
                                             float pos2Y, float pos2Z, float pos3X, float pos3Y, float pos3Z, float pos4X, float pos4Y,
                                             float pos4Z, Color color) {

        if (matrix4f != null) {
            tes.addVertex(matrix4f, pos1X, pos1Y, pos1Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(matrix4f, pos2X, pos2Y, pos2Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(matrix4f, pos3X, pos3Y, pos3Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(matrix4f, pos4X, pos4Y, pos4Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

            tes.addVertex(matrix4f, pos4X, pos4Y, pos4Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(matrix4f, pos3X, pos3Y, pos3Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(matrix4f, pos2X, pos2Y, pos2Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(matrix4f, pos1X, pos1Y, pos1Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        } else {
            tes.addVertex(pos1X, pos1Y, pos1Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(pos2X, pos2Y, pos2Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(pos3X, pos3Y, pos3Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(pos4X, pos4Y, pos4Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

            tes.addVertex(pos4X, pos4Y, pos4Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(pos3X, pos3Y, pos3Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(pos2X, pos2Y, pos2Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            tes.addVertex(pos1X, pos1Y, pos1Z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
    }

    public static void drawQuadGradient(VertexConsumer buf, Matrix4f mat,
                                        Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4,
                                        Color c1, Color c2) {

        int light = LightCoordsUtil.FULL_BRIGHT;

        float nx = 0;
        float ny = 1;
        float nz = 0;

        vertex(buf, mat, (float)p1.x,(float)p1.y,(float)p1.z,c1,0,0,light,nx,ny,nz);
        vertex(buf, mat, (float)p2.x,(float)p2.y,(float)p2.z,c1,1,0,light,nx,ny,nz);
        vertex(buf, mat, (float)p3.x,(float)p3.y,(float)p3.z,c2,1,1,light,nx,ny,nz);
        vertex(buf, mat, (float)p4.x,(float)p4.y,(float)p4.z,c2,0,1,light,nx,ny,nz);
    }

    private static void vertex(VertexConsumer buf, Matrix4f mat,
                               float x, float y, float z,
                               Color c,
                               float u, float v,
                               int light,
                               float nx, float ny, float nz) {

        buf.addVertex(mat, x, y, z)
                .setColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha())
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(nx, ny, nz);
    }
}
