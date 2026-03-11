package it.hurts.shatterbyte.shatterlib.module.particle.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.shatterbyte.shatterlib.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.shatterlib.module.particle.RenderProvider;
import it.hurts.shatterbyte.shatterlib.util.ColorUtils;
import it.hurts.shatterbyte.shatterlib.util.TesselatorUtils;
import it.hurts.shatterbyte.shatterlib.util.VectorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class TrailProvider implements RenderProvider<TrailProvider, TrailBuffer> {
    private static final double EPSILON = 1.0E-8;

    @Override
    public TrailBuffer createBuffer() {
        return new DefaultTrailBuffer(getTrailMaxLength());
    }

    @Override
    public boolean shouldRender(TrailBuffer buffer) {
        return isTrailAlive() || (!disappearAfterDeath() && buffer.size() != 0);
    }

    @Override
    public Vec3 getRenderPosition(float partialTick) {
        return getTrailPosition(partialTick);
    }

    public abstract Vec3 getTrailPosition(float partialTick);

    @Override
    @Deprecated
    public double getRenderDistance() {
        return getTrailRenderDistance();
    }

    public double getTrailRenderDistance() {
        return 64;
    }

    @Override
    @Deprecated
    public int getUpdateFrequency() {
        return Math.max(1, getTrailUpdateFrequency());
    }

    public int getTrailUpdateFrequency() {
        return 1;
    }

    public abstract boolean isTrailAlive();

    public boolean isTrailGrowing() {
        return true;
    }

    public boolean disappearAfterDeath() {
        return false;
    }

    public abstract int getTrailMaxLength();

    public abstract int getTrailFadeInColor();

    public abstract int getTrailFadeOutColor();

    public abstract double getTrailScale();

    public int getTrailInterpolationPoints() {
        return 1;
    }

    public double getTrailSampleMinDistance() {
        return Math.max(0.01, getTrailScale() * 0.25);
    }

    public double getTrailLifetimeTicks() {
        return Math.max(1.0, getTrailMaxLength() * Math.max(1, getTrailUpdateFrequency()));
    }

    public int getTrailMaxPointCount() {
        return Math.max(8, getTrailMaxLength() * 8);
    }

    public List<Vec3> getTrailRenderPositions(List<Vec3> points, float pTicks) {
        return points;
    }

    @Override
    @Deprecated
    public void render(float pTicks, PoseStack poseStack, VertexConsumer consumer) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null || getTrailMaxLength() <= 0) return;

        TrailBuffer buffer = ShatterRenderManager.getOrCreateBuffer(this);
        if (buffer == null) return;

        buffer.renderTick(this, pTicks);

        var points = collectRenderPoints(buffer, pTicks);
        if (points.size() < 2) return;

        var smoothPoints = buildSmoothedPoints(points);
        if (smoothPoints.size() < 2) return;

        var origin = getRenderPosition(pTicks);
        var cameraLocal = Minecraft.getInstance().gameRenderer.getMainCamera().position().subtract(origin);
        var ribbon = buildRibbonFrames(smoothPoints, cameraLocal);
        if (ribbon.size() < 2) return;

        drawRibbon(ribbon, poseStack.last().pose(), consumer);
    }

    private List<Vec3> collectRenderPoints(TrailBuffer buffer, float partialTick) {
        var origin = getRenderPosition(partialTick);
        var points = new ArrayList<Vec3>(buffer.size() + 1);

        if (isTrailAlive()) {
            points.add(Vec3.ZERO);
        }

        for (var point : buffer) {
            if (point != null) {
                points.add(point.subtract(origin));
            }
        }

        return compactPoints(getTrailRenderPositions(points, partialTick));
    }

    private List<Vec3> buildSmoothedPoints(List<Vec3> points) {
        if (points.size() < 3) {
            return points;
        }

        var smooth = new ArrayList<Vec3>();

        for (int i = 0; i < points.size() - 1; i++) {
            var p0 = points.get(Math.max(0, i - 1));
            var p1 = points.get(i);
            var p2 = points.get(i + 1);
            var p3 = points.get(Math.min(points.size() - 1, i + 2));

            if (smooth.isEmpty()) {
                smooth.add(p1);
            }

            int subdivisions = getSegmentSubdivisions(p1, p2);
            for (int j = 1; j <= subdivisions; j++) {
                float t = j / (float) (subdivisions + 1);
                smooth.add(VectorUtils.catmullromVec(t, p0, p1, p2, p3));
            }

            smooth.add(p2);
        }

        return compactPoints(smooth);
    }

    private int getSegmentSubdivisions(Vec3 p1, Vec3 p2) {
        int base = Math.max(0, getTrailInterpolationPoints());
        var segmentLength = p1.distanceTo(p2);
        var width = Math.max(0.02, getTrailScale());
        var adaptive = Mth.clamp((int) Math.floor(segmentLength / (width * 0.75)), 0, 8);

        return Math.max(base, adaptive);
    }

    private List<RibbonFrame> buildRibbonFrames(List<Vec3> points, Vec3 cameraLocal) {
        var frames = new ArrayList<RibbonFrame>(points.size());
        var fallbackTangent = VectorUtils.Z_VEC;

        for (int i = 0; i < points.size(); i++) {
            var current = points.get(i);
            var previous = points.get(Math.max(0, i - 1));
            var next = points.get(Math.min(points.size() - 1, i + 1));

            var tangent = safeNormalize(next.subtract(previous), fallbackTangent);
            if (tangent.lengthSqr() > EPSILON) {
                fallbackTangent = tangent;
            }

            var view = cameraLocal.subtract(current);
            var side = tangent.cross(view);

            if (side.lengthSqr() < EPSILON) {
                side = tangent.cross(VectorUtils.Y_VEC);
            }
            if (side.lengthSqr() < EPSILON) {
                side = tangent.cross(VectorUtils.X_VEC);
            }

            side = safeNormalize(side, VectorUtils.X_VEC);
            if (side.lengthSqr() < EPSILON) {
                continue;
            }

            var progress = i / (double) Math.max(1, points.size() - 1);
            var taper = 1.0 - progress * progress;
            var halfWidth = Math.max(0.0015, getTrailScale() * taper);
            var offset = side.scale(halfWidth);

            frames.add(new RibbonFrame(current.add(offset), current.subtract(offset)));
        }

        return frames;
    }

    private void drawRibbon(List<RibbonFrame> ribbon, Matrix4f matrix4f, VertexConsumer consumer) {
        var startColor = new Color(getTrailFadeInColor(), true);
        var endColor = new Color(getTrailFadeOutColor(), true);

        for (int i = 0; i < ribbon.size() - 1; i++) {
            var current = ribbon.get(i);
            var next = ribbon.get(i + 1);

            var t0 = i / (float) (ribbon.size() - 1);
            var t1 = (i + 1) / (float) (ribbon.size() - 1);

            var c0 = ColorUtils.blend(startColor, endColor, t0);
            var c1 = ColorUtils.blend(startColor, endColor, t1);

            TesselatorUtils.drawQuadGradient(consumer, matrix4f,
                    next.left,
                    current.left,
                    current.right,
                    next.right,
                    c1, c0);

            TesselatorUtils.drawQuadGradient(consumer, matrix4f,
                    next.right,
                    current.right,
                    current.left,
                    next.left,
                    c1, c0);
        }
    }

    private List<Vec3> compactPoints(List<Vec3> points) {
        var compacted = new ArrayList<Vec3>(points.size());

        for (var point : points) {
            if (point == null || !Double.isFinite(point.x) || !Double.isFinite(point.y) || !Double.isFinite(point.z)) {
                continue;
            }

            if (compacted.isEmpty() || compacted.get(compacted.size() - 1).distanceToSqr(point) > EPSILON) {
                compacted.add(point);
            }
        }

        return compacted;
    }

    private Vec3 safeNormalize(Vec3 vector, Vec3 fallback) {
        if (vector.lengthSqr() > EPSILON) {
            return vector.normalize();
        }

        if (fallback.lengthSqr() > EPSILON) {
            return fallback.normalize();
        }

        return VectorUtils.Z_VEC;
    }

    private record RibbonFrame(Vec3 left, Vec3 right) {
    }
}
