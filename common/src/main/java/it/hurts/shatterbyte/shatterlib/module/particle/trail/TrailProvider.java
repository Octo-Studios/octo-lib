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
import java.util.Objects;

import static it.hurts.shatterbyte.shatterlib.util.VectorUtils.Y_VEC;

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

        var renderTime = world.getGameTime() - 1 + pTicks;
        var points = collectRenderPoints(buffer, pTicks, renderTime);
        if (points.positions().size() < 2) return;

        var smoothPoints = buildSmoothedPoints(points);
        if (smoothPoints.positions().size() < 2) return;

        var origin = getRenderPosition(pTicks);
        var cameraLocal = Minecraft.getInstance().gameRenderer.mainCamera().position().subtract(origin);
        var ribbon = buildRibbonFrames(smoothPoints, cameraLocal);
        if (ribbon.size() < 2) return;

        drawRibbon(ribbon, poseStack.last().pose(), consumer);
    }

    private RenderPoints collectRenderPoints(TrailBuffer buffer, float partialTick, double renderTime) {
        var origin = getRenderPosition(partialTick);
        var maxAge = Math.max(1.0, getTrailLifetimeTicks());

        var positions = new ArrayList<Vec3>(buffer.size() + 1);
        var ages = new ArrayList<Double>(buffer.size() + 1);
        Vec3 lead = null;

        if (isTrailAlive()) {
            positions.add(Vec3.ZERO);
            ages.add(0.0);
        }

        for (var sample : buffer) {
            if (sample == null) {
                continue;
            }

            var position = sample.position().subtract(origin);
            var age = renderTime - sample.time();

            if (age < 0) {
                lead = position;
                continue;
            }

            if (age > maxAge) {
                if (!positions.isEmpty()) {
                    var lastAge = ages.get(ages.size() - 1);
                    var t = (maxAge - lastAge) / Math.max(EPSILON, age - lastAge);

                    positions.add(positions.get(positions.size() - 1).lerp(position, Mth.clamp(t, 0.0, 1.0)));
                    ages.add(maxAge);
                }

                break;
            }

            positions.add(position);
            ages.add(age);
        }

        var custom = getTrailRenderPositions(positions, partialTick);
        var size = Math.min(custom.size(), ages.size());

        return compactPoints(new RenderPoints(new ArrayList<>(custom.subList(0, size)), new ArrayList<>(ages.subList(0, size)), lead));
    }

    private RenderPoints buildSmoothedPoints(RenderPoints points) {
        var positions = points.positions();
        var ages = points.ages();

        if (positions.size() < 2) {
            return points;
        }

        var smoothPositions = new ArrayList<Vec3>();
        var smoothAges = new ArrayList<Double>();

        smoothPositions.add(positions.get(0));
        smoothAges.add(ages.get(0));

        for (int i = 0; i < positions.size() - 1; i++) {
            var p0 = i == 0 ? (points.lead() != null ? points.lead() : positions.get(0)) : positions.get(i - 1);
            var p1 = positions.get(i);
            var p2 = positions.get(i + 1);
            var p3 = positions.get(Math.min(positions.size() - 1, i + 2));

            var a1 = ages.get(i);
            var a2 = ages.get(i + 1);

            int subdivisions = getSegmentSubdivisions(p1, p2);
            for (int j = 1; j <= subdivisions; j++) {
                float t = j / (float) (subdivisions + 1);

                smoothPositions.add(VectorUtils.catmullromVec(t, p0, p1, p2, p3));
                smoothAges.add(Mth.lerp(t, a1, a2));
            }

            smoothPositions.add(p2);
            smoothAges.add(a2);
        }

        return compactPoints(new RenderPoints(smoothPositions, smoothAges, null));
    }

    private int getSegmentSubdivisions(Vec3 p1, Vec3 p2) {
        int base = Math.max(0, getTrailInterpolationPoints());
        var segmentLength = p1.distanceTo(p2);
        var width = Math.max(0.02, getTrailScale());
        var adaptive = Mth.clamp((int) Math.floor(segmentLength / (width * 0.75)), 0, 8);

        return Math.max(base, adaptive);
    }

    private List<RibbonFrame> buildRibbonFrames(RenderPoints points, Vec3 cameraLocal) {
        var positions = points.positions();
        var ages = points.ages();
        var frames = new ArrayList<RibbonFrame>(positions.size());
        var fallbackTangent = VectorUtils.Z_VEC;
        var oldestAge = Math.max(EPSILON, ages.get(ages.size() - 1));

        for (int i = 0; i < positions.size(); i++) {
            var current = positions.get(i);
            var previous = positions.get(Math.max(0, i - 1));
            var next = positions.get(Math.min(positions.size() - 1, i + 1));

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

            var progress = Mth.clamp(ages.get(i) / oldestAge, 0.0, 1.0);
            var taper = 1.0 - progress * progress;
            var halfWidth = Math.max(0.0015, getTrailScale() * taper);
            var offset = side.scale(halfWidth);

            frames.add(new RibbonFrame(current.add(offset), current.subtract(offset), (float) progress));
        }

        return frames;
    }

    private void drawRibbon(List<RibbonFrame> ribbon, Matrix4f matrix4f, VertexConsumer consumer) {
        var startColor = new Color(getTrailFadeInColor(), true);
        var endColor = new Color(getTrailFadeOutColor(), true);

        for (int i = 0; i < ribbon.size() - 1; i++) {
            var current = ribbon.get(i);
            var next = ribbon.get(i + 1);

            var c0 = ColorUtils.blend(startColor, endColor, current.progress());
            var c1 = ColorUtils.blend(startColor, endColor, next.progress());

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

    private RenderPoints compactPoints(RenderPoints points) {
        var positions = new ArrayList<Vec3>(points.positions().size());
        var ages = new ArrayList<Double>(points.ages().size());

        for (int i = 0; i < points.positions().size(); i++) {
            var point = points.positions().get(i);

            if (point == null || !Double.isFinite(point.x) || !Double.isFinite(point.y) || !Double.isFinite(point.z)) {
                continue;
            }

            if (positions.isEmpty() || positions.get(positions.size() - 1).distanceToSqr(point) > EPSILON) {
                positions.add(point);
                ages.add(points.ages().get(i));
            }
        }

        return new RenderPoints(positions, ages, points.lead());
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

    private record RenderPoints(List<Vec3> positions, List<Double> ages, Vec3 lead) {
    }

    private record RibbonFrame(Vec3 left, Vec3 right, float progress) {
    }
}