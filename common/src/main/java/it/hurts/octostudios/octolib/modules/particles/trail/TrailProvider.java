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
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static it.hurts.octostudios.octolib.util.TesselatorUtils.TRAIL_RENDER_TYPE;
import static it.hurts.octostudios.octolib.util.VectorUtils.X_VEC;
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
    
    default List<Vec3> getTrailRenderPositions(List<Vec3> points, float pTicks) {
        if (points.size() < 3) return points;
        List<Vec3> interpolated = new ArrayList<>(List.of(points.get(0)));
        for (int i = 1; i < points.size()-2; i++) {
            interpolated.add(points.get(i+1).lerp(points.get(i), pTicks));
        }
        return interpolated;
    }
    
    @Override
    @Deprecated
    default void render(float pTicks, PoseStack poseStack, MultiBufferSource bufferSourceList) {
        renderTrail(pTicks, poseStack, bufferSourceList);
    }

    default void renderTrail(float pTicks, PoseStack poseStack, MultiBufferSource bufferSourceList) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) return;

        Vec3 matrixTranslation = getRenderPosition(pTicks);
        long time = world.getGameTime();
        int segments = getTrailMaxLength();

        if (segments <= 0 || getTrailUpdateFrequency() <= 0) return;

        List<Vec3> partialPoses = new ArrayList<>();
        float partial = (time % getTrailUpdateFrequency()) + pTicks;

        TrailBuffer buffer = OctoRenderManager.getOrCreateBuffer(this);
        if (buffer == null) return;

        List<Vec3> points = new ArrayList<>();
        if (isTrailAlive()) points.add(new Vec3(0, 0, 0));

        for (Vec3 vec3 : buffer) {
            points.add(vec3.subtract(matrixTranslation));
        }

        points = getTrailRenderPositions(points, pTicks);

        if (points.size() > 2) {
            for (int i = 0; i < points.size() - 1; i++) {
                Vec3 p0 = i == 0 ? points.getFirst() : points.get(i - 1);
                Vec3 p1 = points.get(i);
                Vec3 p2 = points.get(i + 1);
                Vec3 p3 = i == points.size() - 2 ? points.getLast() : points.get(i + 2);

                partialPoses.add(p1);
                int interpolationPoints = Math.max(1, getTrailInterpolationPoints() + 1);
                for (float f = 1f / interpolationPoints; f < 1; f += 1f / interpolationPoints) {
                    partialPoses.add(VectorUtils.catmullromVec(f, p0, p1, p2, p3));
                }
            }
            partialPoses.add(points.getLast());
        } else {
            partialPoses.addAll(points);
        }

        if (points.size() > 1 && getTrailMaxLength() + 1 == points.size()) {
            int i = partialPoses.size() - 1;
            Vec3 adjustment = partialPoses.get(i - 1).subtract(partialPoses.get(i))
                    .scale(partial / (double) getTrailUpdateFrequency() * getTrailInterpolationPoints());
            partialPoses.set(i, partialPoses.get(i).add(adjustment));
        }

        draw3dTrail(partialPoses, poseStack, bufferSourceList);
    }


    default void draw3dTrail(List<Vec3> partialPoses, PoseStack poseStack, MultiBufferSource bufferSourceList) {
        if (partialPoses == null || partialPoses.size() < 2) return;

        partialPoses = partialPoses.stream().filter(Objects::nonNull).toList();

        if (partialPoses.size() < 2)
            return;

        var crossVecs = new Vec3[partialPoses.size()][3];

        for (var i = 1; i < partialPoses.size(); i++) {
            var pos1 = partialPoses.get(i - 1);
            var pos2 = partialPoses.get(i);

            if (pos1 == null || pos2 == null) continue;

            var vec1 = pos2.subtract(pos1);
            var vec1n = vec1.normalize();

            var perpendicular1 = vec1n.cross(Y_VEC).normalize();

            var scale = getTrailScale() * (1.0 - (double) i / (partialPoses.size() - 1))+0.005;

            crossVecs[i - 1][0] = perpendicular1.scale(scale);
            crossVecs[i - 1][1] = VectorUtils.rotate(crossVecs[i-1][0], vec1n, 120);
            crossVecs[i - 1][2] = VectorUtils.rotate(crossVecs[i-1][0], vec1n, -120);
        }

        var color1 = new Color(getTrailFadeInColor(), true);
        var color2 = new Color(getTrailFadeOutColor(), true);

        poseStack.pushPose();

        var matrix4f = poseStack.last().pose();
        var tes = bufferSourceList.getBuffer(TRAIL_RENDER_TYPE);

        for (var i = 0; i < partialPoses.size() - 1; i++) {
            var pos1 = partialPoses.get(i);
            var pos2 = partialPoses.get(i + 1);

            if (pos1 == null || pos2 == null || crossVecs[i][0] == null || crossVecs[i][1] == null || crossVecs[i + 1][0] == null || crossVecs[i + 1][1] == null)
                continue;

            var tip1 = pos1.add(crossVecs[i][0]);
            var base1Left = pos1.add(crossVecs[i][2]);
            var base1Right = pos1.add(crossVecs[i][1]);

            var tip2 = pos2.add(crossVecs[i + 1][0]);
            var base2Left = pos2.add(crossVecs[i + 1][2]);
            var base2Right = pos2.add(crossVecs[i + 1][1]);

            var c1 = ColorUtils.blend(color1, color2, i / (float) (partialPoses.size() - 1));
            var c2 = ColorUtils.blend(color1, color2, (i + 1) / (float) (partialPoses.size() - 1));

            TesselatorUtils.drawQuadGradient(tes, matrix4f,
                    (float) base2Left.x, (float) base2Left.y, (float) base2Left.z,
                    (float) base1Left.x, (float) base1Left.y, (float) base1Left.z,
                    (float) tip1.x, (float) tip1.y, (float) tip1.z,
                    (float) tip2.x, (float) tip2.y, (float) tip2.z, c2, c1);

            TesselatorUtils.drawQuadGradient(tes, matrix4f,
                    (float) tip2.x, (float) tip2.y, (float) tip2.z,
                    (float) tip1.x, (float) tip1.y, (float) tip1.z,
                    (float) base1Right.x, (float) base1Right.y, (float) base1Right.z,
                    (float) base2Right.x, (float) base2Right.y, (float) base2Right.z,
                    c2, c1);

            TesselatorUtils.drawQuadGradient(tes, matrix4f,
                    (float) base2Right.x, (float) base2Right.y, (float) base2Right.z,
                    (float) base1Right.x, (float) base1Right.y, (float) base1Right.z,
                    (float) base1Left.x, (float) base1Left.y, (float) base1Left.z,
                    (float) base2Left.x, (float) base2Left.y, (float) base2Left.z,
                    c2, c1);
        }

        poseStack.popPose();
    }
    
}
