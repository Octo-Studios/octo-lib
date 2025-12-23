package it.hurts.octostudios.octolib.module.particle.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.octolib.module.particle.OctoRenderManager;
import it.hurts.octostudios.octolib.module.particle.RenderProvider;
import it.hurts.octostudios.octolib.util.ColorUtils;
import it.hurts.octostudios.octolib.util.TesselatorUtils;
import it.hurts.octostudios.octolib.util.VectorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static it.hurts.octostudios.octolib.util.TesselatorUtils.TRAIL_RENDER_TYPE;
import static it.hurts.octostudios.octolib.util.VectorUtils.Y_VEC;

public abstract class TrailProvider implements RenderProvider<TrailProvider, TrailBuffer> {
    @Override
    public TrailBuffer createBuffer() {
        return new DefaultTrailBuffer<>(getTrailMaxSamples());
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

    public abstract int getTrailUpdateFrequency();

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

    public double getTrailMaxAgeTicks() {
        return (double) getTrailMaxLength() * Math.max(1, getTrailUpdateFrequency());
    }
    
    public double getTrailEffectiveMaxAgeTicks(boolean growing) {
        var baseMaxAge = Math.max(1.0, getTrailMaxAgeTicks());
        if (growing) return baseMaxAge;
        
        return Math.min(baseMaxAge * getTrailInactiveFadeMultiplier(), getTrailInactiveMaxAgeTicks());
    }

    public double getTrailMinSampleDistance() {
        return Math.max(0.01, getTrailScale() * 0.15);
    }

    public int getTrailSamplesPerTick() {
        return 3;
    }

    public int getTrailMaxSamples() {
        return (int) Math.ceil(getTrailMaxAgeTicks() * Math.max(1, getTrailSamplesPerTick()));
    }

    public double getTrailInactiveFadeMultiplier() {
        return 1;
    }

    public double getTrailInactiveMaxAgeTicks() {
        return getTrailMaxAgeTicks();
    }

    public double getTrailWidthPower() {
        return 1;
    }

    public int getTrailFaces() {
        return 3;
    }

    public List<Vec3> getTrailRenderPositions(List<Vec3> points, float pTicks) {
        return points;
    }

    @Override
    @Deprecated
    public void render(float pTicks, PoseStack poseStack, MultiBufferSource bufferSourceList) {
        var world = Minecraft.getInstance().level;
        if (world == null) return;

        var matrixTranslation = this.getRenderPosition(pTicks);
        var now = world.getGameTime() + pTicks;
        
        if (this.getTrailMaxLength() <= 0) return;

        var buffer = OctoRenderManager.getOrCreateBuffer(this);

        if (buffer == null)
            return;

        var aliveNow = this.isTrailAlive();
        var growingNow = aliveNow && this.isTrailGrowing();
        var minDistance = getTrailMinSampleDistance();
        var minDistanceSq = minDistance * minDistance;

        if (growingNow) {
            var head = buffer.peekFirst();
            if (head == null) {
                buffer.write(matrixTranslation, now);
            } else {
                var distSq = head.position().distanceToSqr(matrixTranslation);
                if (distSq >= minDistanceSq && now > head.time()) {
                    var samplesPerTick = Math.max(1, getTrailSamplesPerTick());
                    var sampleInterval = 1.0 / samplesPerTick;
                    var deltaTime = Math.max(1e-6, now - head.time());
                    var stepsByTime = (int) Math.floor(deltaTime / sampleInterval);
                    
                    if (stepsByTime > 0) {
                        var stepsByDistance = (int) Math.ceil(Math.sqrt(distSq) / minDistance);
                        var steps = Math.min(stepsByTime, Math.max(1, stepsByDistance));
                        
                        for (int i = 1; i <= steps; i++) {
                            var sampleTime = head.time() + sampleInterval * i;
                            if (sampleTime > now) break;
                            double t = (sampleTime - head.time()) / deltaTime;
                            var samplePos = head.position().lerp(matrixTranslation, t);
                            buffer.write(samplePos, sampleTime);
                        }
                    }
                }
            }
        }
        
        var maxAge = getTrailEffectiveMaxAgeTicks(growingNow);
        buffer.pruneOlderThan(now - maxAge);
        buffer.trimToSize(this.getTrailMaxSamples());

        var points = new ArrayList<Vec3>();
        var lifeFractions = new ArrayList<Float>();
        
        if (aliveNow) {
            var headLife = 0f;
            if (!growingNow && buffer.peekFirst() != null) {
                headLife = (float) Mth.clamp((now - buffer.peekFirst().time()) / maxAge, 0.0, 1.0);
            }
            points.add(Vec3.ZERO);
            lifeFractions.add(headLife);
        }

        for (var sample : buffer) {
            var relative = sample.position().subtract(matrixTranslation);
            var life = (float) Mth.clamp((now - sample.time()) / maxAge, 0.0, 1.0);
            points.add(relative);
            lifeFractions.add(life);
        }

        if (points.size() < 2) return;

        points = new ArrayList<>(this.getTrailRenderPositions(points, pTicks));
        if (points.size() != lifeFractions.size()) {
            var size = Math.min(points.size(), lifeFractions.size());
            points = new ArrayList<>(points.subList(0, size));
            lifeFractions = new ArrayList<>(lifeFractions.subList(0, size));
        }
        
        if (points.size() > 1) {
            var densePoints = new ArrayList<Vec3>(points.size());
            var denseLives = new ArrayList<Float>(lifeFractions.size());
            var maxSteps = Math.max(2, getTrailSamplesPerTick() * 4);
            
            densePoints.add(points.getFirst());
            denseLives.add(lifeFractions.getFirst());
            
            for (int i = 0; i < points.size() - 1; i++) {
                var p1 = points.get(i);
                var p2 = points.get(i + 1);
                var l1 = lifeFractions.get(i);
                var l2 = lifeFractions.get(i + 1);
                var dist = p1.distanceTo(p2);
                
                if (dist > minDistance) {
                    var steps = (int) Math.ceil(dist / minDistance);
                    steps = Math.min(steps, maxSteps);
                    
                    for (int s = 1; s < steps; s++) {
                        float t = s / (float) steps;
                        densePoints.add(p1.lerp(p2, t));
                        denseLives.add(Mth.lerp(t, l1, l2));
                    }
                }
                
                densePoints.add(p2);
                denseLives.add(l2);
            }
            
            points = densePoints;
            lifeFractions = denseLives;
        }

        var partialPoses = new ArrayList<Vec3>();
        var partialLives = new ArrayList<Float>();

        if (points.size() > 2) {
            for (var i = 0; i < points.size() - 1; i++) {
                var p0 = i == 0 ? points.getFirst() : points.get(i - 1);
                var p1 = points.get(i);
                var p2 = points.get(i + 1);
                var p3 = i == points.size() - 2 ? points.getLast() : points.get(i + 2);

                var l1 = lifeFractions.get(i);
                var l2 = lifeFractions.get(i + 1);

                partialPoses.add(p1);
                partialLives.add(l1);

                var interpolationPoints = Math.max(1, this.getTrailInterpolationPoints() + 1);
                for (float f = 1f / interpolationPoints; f < 1; f += 1f / interpolationPoints) {
                    partialPoses.add(VectorUtils.catmullromVec(f, p0, p1, p2, p3));
                    partialLives.add(Mth.lerp(f, l1, l2));
                }
            }
            partialPoses.add(points.getLast());
            partialLives.add(lifeFractions.getLast());
        } else {
            partialPoses.addAll(points);
            partialLives.addAll(lifeFractions);
        }

        if (partialPoses.size() < 2) return;

        var cleanPoses = new ArrayList<Vec3>();
        var cleanLives = new ArrayList<Float>();
        for (var i = 0; i < partialPoses.size(); i++) {
            var v = partialPoses.get(i);
            if (v == null) continue;
            cleanPoses.add(v);
            cleanLives.add(partialLives.get(i));
        }

        partialPoses = cleanPoses;
        partialLives = cleanLives;

        if (partialPoses.size() < 2) return;

        var faces = Math.max(1, getTrailFaces());
        var ringSize = faces == 1 ? 2 : faces;
        var crossVecs = new Vec3[partialPoses.size()][ringSize];
        var lastIndex = partialPoses.size() - 1;
        var baseScale = this.getTrailScale();

        var widthPower = Math.max(0.05, getTrailWidthPower());
        var angleStep = faces == 1 ? 180.0 : 360.0 / faces;

        for (var i = 0; i < partialPoses.size(); i++) {
            var current = partialPoses.get(i);
            if (current == null) continue;

            var prev = i == 0 ? current : partialPoses.get(i - 1);
            var next = i == lastIndex ? current : partialPoses.get(i + 1);

            var tangent = next.subtract(prev);
            if (tangent.lengthSqr() < 1e-6) continue;

            var perpendicular = tangent.cross(Y_VEC);
            if (perpendicular.lengthSqr() < 1e-6) {
                perpendicular = tangent.cross(VectorUtils.X_VEC);
            }

            if (perpendicular.lengthSqr() < 1e-6) continue;

            var axis = tangent.normalize();
            var base = perpendicular.normalize();

            var ratio = 1.0 - Math.pow((double) i / Math.max(1, lastIndex), widthPower);
            var scale = baseScale * ratio;
            if (scale <= 1e-6) {
                continue;
            }

            var first = base.scale(scale);
            for (var j = 0; j < ringSize; j++) {
                crossVecs[i][j] = VectorUtils.rotate(first, axis, angleStep * j);
            }
        }

        var color1 = new Color(this.getTrailFadeInColor(), true);
        var color2 = new Color(this.getTrailFadeOutColor(), true);

        poseStack.pushPose();

        var matrix4f = poseStack.last().pose();
        var tes = bufferSourceList.getBuffer(TRAIL_RENDER_TYPE);

        for (var i = 0; i < partialPoses.size() - 1; i++) {
            var pos1 = partialPoses.get(i);
            var pos2 = partialPoses.get(i + 1);

            if (pos1 == null || pos2 == null) continue;
            if (pos2.subtract(pos1).lengthSqr() < 1e-6) continue;

            var t1 = i / (float) (partialPoses.size() - 1);
            var t2 = (i + 1) / (float) (partialPoses.size() - 1);
            var c1 = ColorUtils.blend(color1, color2, t1);
            var c2 = ColorUtils.blend(color1, color2, t2);

            for (var j = 0; j < ringSize; j++) {
                var jNext = (j + 1) % ringSize;
                var o1a = crossVecs[i][j];
                var o1b = crossVecs[i][jNext];
                var o2a = crossVecs[i + 1][j];
                var o2b = crossVecs[i + 1][jNext];
                if (o1a == null || o1b == null || o2a == null || o2b == null) continue;

                TesselatorUtils.drawQuadGradient(tes, matrix4f,
                        (float) (pos2.x + o2b.x), (float) (pos2.y + o2b.y), (float) (pos2.z + o2b.z),
                        (float) (pos1.x + o1b.x), (float) (pos1.y + o1b.y), (float) (pos1.z + o1b.z),
                        (float) (pos1.x + o1a.x), (float) (pos1.y + o1a.y), (float) (pos1.z + o1a.z),
                        (float) (pos2.x + o2a.x), (float) (pos2.y + o2a.y), (float) (pos2.z + o2a.z),
                        c2, c1);
            }
        }

        poseStack.popPose();
    }
}
