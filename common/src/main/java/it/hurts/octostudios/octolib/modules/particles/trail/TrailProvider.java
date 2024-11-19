package it.hurts.octostudios.octolib.modules.particles.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import it.hurts.octostudios.octolib.modules.particles.RenderProvider;
import it.hurts.octostudios.octolib.util.ColorUtils;
import it.hurts.octostudios.octolib.util.TesselatorUtils;
import it.hurts.octostudios.octolib.util.VectorUtils;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static it.hurts.octostudios.octolib.util.TesselatorUtils.TRAIL_RENDER_TYPE;
import static it.hurts.octostudios.octolib.util.VectorUtils.Y_VEC;

public interface TrailProvider extends RenderProvider<TrailProvider, TrailBuffer> {

    default TrailBuffer createBuffer() {
        return new DefaultTrailBuffer(getTrailMaxLength());
    }

    @Override
    default boolean shouldRender(TrailBuffer buffer) {
        return isTrailAlive() || (!disappearAfterDeath() && buffer.size() != 0);
    }

    @Override
    default Vec3 getRenderPosition(float partialTick) {
        return getTrailPosition(partialTick);
    }

    Vec3 getTrailPosition(float partialTick);

    @Override
    @Deprecated
    default double getRenderDistance() {
        return getTrailRenderDistance();
    }

    default double getTrailRenderDistance() {
        return 64;
    }

    @Override
    @Deprecated
    default int getUpdateFrequency() {
        return getTrailUpdateFrequency();
    }

    int getTrailUpdateFrequency();

    boolean isTrailAlive();
    
    default boolean isTrailGrowing() {
        return true;
    }

    default boolean disappearAfterDeath() {
        return false;
    }

    int getTrailMaxLength();

    int getTrailFadeInColor();

    int getTrailFadeOutColor();

    double getTrailScale();

    default int getTrailInterpolationPoints() {
        return 1;
    }
    
    default Vec3 getPointRenderOffset(int pointIndex, Vec3 relativePosition) {
        return new Vec3(0, 0, 0);
    }

    @Override
    @Deprecated
    default void render(float pTicks, PoseStack poseStack, MultiBufferSource bufferSourceList) {
        renderTrail(pTicks, poseStack, bufferSourceList);
    }

    default void renderTrail(float pTicks, PoseStack poseStack, MultiBufferSource bufferSourceList) {
        ClientLevel world = Minecraft.getInstance().level;
        Vec3 matrixTranslation = getRenderPosition(pTicks);

        if (world == null)
            return;

        long time = world.getGameTime();

        int segments = getTrailMaxLength();

        if (segments <= 0 || getTrailUpdateFrequency() <= 0)
            return;

        List<Vec3> partialPoses = new ArrayList<>();

        float partial = (int) (time % getTrailUpdateFrequency()) + pTicks;

        TrailBuffer buffer = OctoRenderManager.getOrCreateBuffer(this);
        List<Vec3> points = new ArrayList<>();
        points.add(new Vec3(0, 0, 0));
        
        buffer.forEach(p -> points.add(p.subtract(matrixTranslation)));
        for (int i = 0; i < points.size(); i++) {
            var p = points.get(i);
            points.set(i, p.add(getPointRenderOffset(i, p)));
        }
        
        if (points.size() > 2) {
            for (int i = 0; i < points.size() - 1; i++) {
                var p0 = i == 0 ? points.getFirst() : points.get(i - 1);
                var p1 = points.get(i);
                var p2 = points.get(i + 1);
                var p3 = i == points.size() - 2 ? points.getLast() : points.get(i + 2);

                partialPoses.add(p1);
                float p = (getTrailInterpolationPoints() + 1);
                for (float f = p; f < 1; f += 1f / p)
                    partialPoses.add(VectorUtils.catmullromVec(f, p0, p1, p2, p3));
            }
            partialPoses.add(points.getLast());
        } else
            partialPoses.addAll(points);


        if (points.size() > 1 && getTrailMaxLength() + 1 == points.size()) {
            int i = partialPoses.size() - 1;
            partialPoses.set(i, partialPoses.get(i).add(
                    partialPoses.get(i - 1).subtract(partialPoses.get(i)).scale(partial / (double) getTrailUpdateFrequency()
                    * (double) getTrailInterpolationPoints())));
        }

//        float deathPartial = (segmentsDisappearingSpeed - streakable.ticksBeforeDeath() % segmentsDisappearingSpeed) - 1 + pTicks;

        draw3dTrail(partialPoses, poseStack, bufferSourceList);
    }

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
                    .scale(getTrailScale() * len);
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

        Color color1 = new Color(getTrailFadeInColor(), true);
        Color color2 = new Color(getTrailFadeOutColor(), true);

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
                Vec3 pos13 = partialPoses.get(i).add(crossVecs[i][2]);
                Vec3 pos2 = partialPoses.get(i + 1);
                
                TesselatorUtils.drawQuadGradient(tes, matrix4f, (float) pos11.x, (float) pos11.y, (float) pos11.z, (float) pos2.x, (float) pos2.y,
                        (float) pos2.z, (float) pos2.x, (float) pos2.y, (float) pos2.z, (float) pos12.x, (float) pos12.y, (float) pos12.z, c1, c2);
                TesselatorUtils.drawQuadGradient(tes, matrix4f, (float) pos12.x, (float) pos12.y, (float) pos12.z, (float) pos2.x, (float) pos2.y,
                        (float) pos2.z, (float) pos2.x, (float) pos2.y, (float) pos2.z, (float) pos13.x, (float) pos13.y, (float) pos13.z, c1, c2);
                TesselatorUtils.drawQuadGradient(tes, matrix4f, (float) pos13.x, (float) pos13.y, (float) pos13.z, (float) pos2.x, (float) pos2.y,
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
