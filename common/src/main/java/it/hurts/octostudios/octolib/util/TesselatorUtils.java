package it.hurts.octostudios.octolib.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

import static net.minecraft.client.renderer.RenderStateShard.LEQUAL_DEPTH_TEST;
import static net.minecraft.client.renderer.RenderStateShard.LIGHTNING_TRANSPARENCY;

public class TesselatorUtils {
    
    public static final RenderType TRAIL_RENDER_TYPE = RenderType.create("octoparticle_trail", DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setOutputState(RenderStateShard.OutputStateShard.MAIN_TARGET)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .createCompositeState(false));
    
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

    /* public static void drawQuad(BufferBuilder tes, IIcon icon, double pos1X, double pos1Y, double pos1Z, double pos2X,
                                double pos2Y, double pos2Z, double pos3X, double pos3Y, double pos3Z, double pos4X, double pos4Y,
                                double pos4Z) {

        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        float minU = icon.getMinU();
        float minV = icon.getMinV();

        tes.addVertexWithUV(pos1X, pos1Y, pos1Z, maxU, maxV);
        tes.addVertexWithUV(pos2X, pos2Y, pos2Z, maxU, minV);
        tes.addVertexWithUV(pos3X, pos3Y, pos3Z, minU, minV);
        tes.addVertexWithUV(pos4X, pos4Y, pos4Z, minU, maxV);

    } */
    
    public static void drawQuadGradient(VertexConsumer tes, Matrix4f matrix4f, float pos1X, float pos1Y, float pos1Z, float pos2X,
                                        float pos2Y, float pos2Z, float pos3X, float pos3Y, float pos3Z, float pos4X, float pos4Y,
                                        float pos4Z, Color color1, Color color2) {
        
        if (matrix4f != null) {
            tes.addVertex(matrix4f,  pos4X,  pos4Y,  pos4Z).setColor(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha());
            tes.addVertex(matrix4f,  pos1X,  pos1Y,  pos1Z).setColor(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha());
            tes.addVertex(matrix4f,  pos2X,  pos2Y,  pos2Z).setColor(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha());
            tes.addVertex(matrix4f,  pos3X,  pos3Y,  pos3Z).setColor(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha());
        } else {
            tes.addVertex(pos4X, pos4Y, pos4Z).setColor(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha());
            tes.addVertex(pos1X, pos1Y, pos1Z).setColor(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha());
            tes.addVertex(pos2X, pos2Y, pos2Z).setColor(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha());
            tes.addVertex(pos3X, pos3Y, pos3Z).setColor(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha());
        }
    }
    
}