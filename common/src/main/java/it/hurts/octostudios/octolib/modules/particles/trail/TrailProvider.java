package it.hurts.octostudios.octolib.modules.particles.trail;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.hurts.octostudios.octolib.util.ColorUtils;
import it.hurts.octostudios.octolib.util.TesselatorUtils;
import it.hurts.octostudios.octolib.util.VectorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static it.hurts.octostudios.octolib.util.VectorUtils.Y_VEC;
import static net.minecraft.client.renderer.RenderStateShard.LEQUAL_DEPTH_TEST;
import static net.minecraft.client.renderer.RenderStateShard.LIGHTNING_TRANSPARENCY;

public interface TrailProvider {
    
    default TrailBuffer createBuffer() {
        return new DefaultTrailBuffer(maxSize());
    }
    
    Vec3 getPointPosition(float f);
    
    default int maxRenderDistance() {
        return 64;
    };
    
    int maxSize();
    
    boolean isAlive();
    
    default boolean shouldBeRemoved() {
        return true;
    }
    
    int fadeIn();
    
    int fadeOut();
    
    int frequency();
    
    double width();
    
    default boolean plain() {
        return false;
    };
    
    default void render(float pTicks, PoseStack poseStack, MultiBufferSource bufferSourceList) {
        renderTrail(pTicks, poseStack, bufferSourceList);
    }
    
    default void renderTrail(float pTicks, PoseStack poseStack, MultiBufferSource bufferSourceList) {
        ClientLevel world = Minecraft.getInstance().level;
        Vec3 matrixTranslation = getPointPosition(pTicks);
        
        if (world == null)
            return;
        
        long time = world.getGameTime();
        
        int segments = maxSize();
        
        if (segments <= 0 || frequency() <= 0)
            return;
        
        List<Vec3> partialPoses = new ArrayList<>();
        
        float partial = (int) (time % frequency()) + pTicks;
        
        var buffer = TrailManager.getOrCreateBuffer(this);
        Iterator<Vec3> iterator = buffer.iterator();
        Vec3 prev = new Vec3(0, 0, 0);
        partialPoses.add(prev);
        
//        if (iterator.hasNext())
//            iterator.next();
        
        while (iterator.hasNext()) {
//            partialPoses.add(iterator.next().subtract(matrixTranslation));
            Vec3 pos = iterator.next().subtract(matrixTranslation);
            Vec3 partialVec = prev;

//            if (buffer.size() == maxSize())
//                partialVec = pos.add(prev.subtract(pos).scale(partial / (double) frequency()));

            partialPoses.add(partialVec);

            prev = pos;
        }
        
        if (partialPoses.size() > 1 && maxSize() == partialPoses.size()) {
            int i  = partialPoses.size() - 1;
            partialPoses.set(i, partialPoses.get(i).add(
                    partialPoses.get(i - 1).subtract(partialPoses.get(i)).scale(partial / (double) frequency())));
        }

//        float deathPartial = (segmentsDisappearingSpeed - streakable.ticksBeforeDeath() % segmentsDisappearingSpeed) - 1 + pTicks;
        
        draw3dTrail(partialPoses, poseStack, bufferSourceList);
    }
    
    RenderType TRAIL_RENDER_TYPE = RenderType.create("octoparticle_trail", DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setOutputState(RenderStateShard.OutputStateShard.MAIN_TARGET)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .createCompositeState(false));
    
    default void draw3dTrail(List<Vec3> partialPoses, PoseStack poseStack, MultiBufferSource bufferSourceList) {
        Vec3[][] crossVecs = new Vec3[partialPoses.size()][3];
        
        for (int i = 1; i < partialPoses.size(); i++) {
            Vec3 pos1 = partialPoses.get(i - 1);
            Vec3 pos2 = partialPoses.get(i);
            Vec3 vec1 = pos2.subtract(pos1);
            Vec3 notScaled = vec1.normalize().equals(new Vec3(0, 1, 0))
                    ? vec1.add(0.0001, 0, 0).cross(Y_VEC)
                    : vec1.cross(Y_VEC).normalize();
            double len = 1 - (i - 1) / (float) partialPoses.size();
            crossVecs[i - 1][0] = notScaled.normalize()
                    .scale(width() * len);
            Vec3 axis = partialPoses.get(i - 1).subtract(partialPoses.get(i));
            crossVecs[i - 1][1] = VectorUtils.rotate(crossVecs[i - 1][0], axis, 120).normalize().scale(crossVecs[i - 1][0].length());
            crossVecs[i - 1][2] = VectorUtils.rotate(crossVecs[i - 1][0], axis, 240).normalize().scale(crossVecs[i - 1][0].length());
        }

//        if (streak.getSegments() > segments) {
//            for (int i = partialPoses.size() - 2; i > 0; i--) {
//                double len = 1 - i / (float) partialPoses.size();
//                double prevLen = 1 - (i - 1) / (float) partialPoses.size(); // prevLen > len
//                double finalLen = Mth.lerp(deathPartial / (float) segmentsDisappearingSpeed, len, prevLen);
//                crossVecs[i][0] = crossVecs[i][0].normalize().scale(streak.getWidth() * finalLen);
//                crossVecs[i][1] = crossVecs[i][1].normalize().scale(streak.getWidth() * finalLen);
//                crossVecs[i][2] = crossVecs[i][1].normalize().scale(streak.getWidth() * finalLen);
//            }
//        }
        
        Color color1 = new Color(fadeIn(), true);
        Color color2 = new Color(fadeOut(), true);
        
        poseStack.pushPose();

        Matrix4f matrix4f = poseStack.last().pose();
        
//        for (int i = 1; i < partialPoses.size(); i++) {
//            VertexConsumer consumer = bufferSourceList.getBuffer(RenderType.lines());
//            LevelRenderer.renderLineBox(poseStack, consumer, new AABB(partialPoses.get(i),
//                    partialPoses.get(i - 1)), 1F, 0F, 0F, 1F);
//        }
        
        VertexConsumer tes = bufferSourceList.getBuffer(TRAIL_RENDER_TYPE);
        for (int i = 0; i < partialPoses.size(); i++) {
            if (crossVecs[i][0] == null)
                break;
            Color c1 = ColorUtils.blend(color1, color2, ((float) i) / (partialPoses.size() - 1));
            Color c2 = ColorUtils.blend(color1, color2, ((float) i + 1) / (partialPoses.size() - 1));

            if (i == crossVecs.length - 1 || crossVecs[i + 1][0] == null) {
                Vec3 pos11 = partialPoses.get(i).add(crossVecs[i][0]);
                Vec3 pos12 = partialPoses.get(i).add(crossVecs[i][1]);
                Vec3 pos13 = partialPoses.get(i).add(crossVecs[i][0].scale(-1));
                Vec3 pos14 = partialPoses.get(i).add(crossVecs[i][1].scale(-1));
                Vec3 pos2 = partialPoses.get(i + 1);

                TesselatorUtils.drawQuadGradient(tes, matrix4f, (float) pos12.x, (float) pos12.y, (float) pos12.z,
                        (float) pos2.x, (float) pos2.y, (float) pos2.z, (float) pos2.x, (float) pos2.y, (float) pos2.z, (float) pos13.x, (float) pos13.y, (float) pos13.z, c1, c2);
                TesselatorUtils.drawQuadGradient(tes, matrix4f, (float) pos11.x, (float) pos11.y, (float) pos11.z, (float) pos2.x, (float) pos2.y,
                        (float) pos2.z, (float) pos2.x, (float) pos2.y, (float) pos2.z, (float) pos12.x, (float) pos12.y, (float) pos12.z, c1, c2);
                TesselatorUtils.drawQuadGradient(tes, matrix4f, (float) pos13.x, (float) pos13.y, (float) pos13.z, (float) pos2.x, (float) pos2.y,
                        (float) pos2.z, (float) pos2.x, (float) pos2.y, (float) pos2.z, (float) pos14.x, (float) pos14.y, (float) pos14.z, c1, c2);
                TesselatorUtils.drawQuadGradient(tes, matrix4f, (float) pos14.x, (float) pos14.y, (float) pos14.z, (float) pos2.x, (float) pos2.y,
                        (float) pos2.z, (float) pos2.x, (float) pos2.y, (float) pos2.z, (float) pos11.x, (float) pos11.y, (float) pos11.z, c1, c2);
            } else {

                Vec3 pos11 = partialPoses.get(i).add(crossVecs[i][0]);
                Vec3 pos12 = partialPoses.get(i).add(crossVecs[i][1]);
                Vec3 pos13 = partialPoses.get(i).add(crossVecs[i][2]);
                /* Vec3 pos13 = prevPoses.get(i).add(crossVecs[i][0].scale(-1));
                Vec3 pos14 = prevPoses.get(i).add(crossVecs[i][1].scale(-1)); */
                Vec3 pos21 = partialPoses.get(i + 1).add(crossVecs[i + 1][0]);
                Vec3 pos22 = partialPoses.get(i + 1).add(crossVecs[i + 1][1]);
                Vec3 pos23 = partialPoses.get(i + 1).add(crossVecs[i + 1][2]);
                /* Vec3 pos23 = prevPoses.get(i + 1).add(crossVecs[i + 1][0].scale(-1));
                Vec3 pos24 = prevPoses.get(i + 1).add(crossVecs[i + 1][1].scale(-1)); */

                /* if (i == 0) {
                    TesselatorUtils.drawQuadGradient(tes, matrix4f, pos11.x, pos11.y, pos11.z, pos12.x, pos12.y,
                            pos12.z, pos13.x, pos13.y, pos13.z, pos14.x, pos14.y, pos14.z, color1, color1);
                } */

                TesselatorUtils.drawQuadGradient(tes, matrix4f, (float) pos11.x, (float) pos11.y, (float) pos11.z, (float) pos21.x, (float) pos21.y,
                        (float) pos21.z, (float) pos22.x, (float) pos22.y, (float) pos22.z, (float) pos12.x, (float) pos12.y, (float) pos12.z, c1, c2);
                TesselatorUtils.drawQuadGradient(tes, matrix4f, (float) pos12.x, (float) pos12.y, (float) pos12.z, (float) pos22.x, (float) pos22.y,
                        (float) pos22.z, (float) pos23.x, (float) pos23.y, (float) pos23.z, (float) pos13.x, (float) pos13.y, (float) pos13.z, c1, c2);
                TesselatorUtils.drawQuadGradient(tes, matrix4f, (float) pos13.x, (float) pos13.y, (float) pos13.z, (float) pos23.x, (float) pos23.y,
                        (float) pos23.z, (float) pos21.x, (float) pos21.y, (float) pos21.z, (float) pos11.x, (float) pos11.y, (float) pos11.z, c1, c2);
                /*TesselatorUtils.drawQuadGradient(tes, matrix4f, pos14.x, pos14.y, pos14.z, pos24.x, pos24.y,
                        pos24.z, pos21.x, pos21.y, pos21.z, pos11.x, pos11.y, pos11.z, c1, c2); */
            }
        }
        poseStack.popPose();
    }
}
