package it.hurts.shatterbyte.shatterlib.module.particle.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.particle.RenderProvider;
import it.hurts.shatterbyte.shatterlib.util.TesselatorUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.submit.BatchableSubmit;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TrailFeatureRenderer extends RenderTypeFeatureRenderer<TrailFeatureRenderer.TrailSubmit> {
    public static final FeatureRendererType<TrailSubmit> TYPE = FeatureRendererType.create(ShatterLib.MOD_ID+":trail_submit");

    @Override
    protected void buildGroup(FeatureFrameContext context, List<TrailSubmit> submits) {
        for (TrailSubmit submit : submits) {
            double d = submit.cameraPos.x();
            double e = submit.cameraPos.y();
            double g = submit.cameraPos.z();

            float f = submit.deltaTracker.getGameTimeDeltaPartialTick(false);

            VertexConsumer builder = this.getVertexBuilder(TesselatorUtils.TRAIL);
            Vec3 position = submit.trail.getRenderPosition(f);
            submit.poseStack.pushPose();
            submit.poseStack.translate(position.x - d, position.y - e, position.z - g);
            submit.trail.render(f, submit.poseStack, builder);
            submit.poseStack.popPose();
        }
    }

    public record TrailSubmit(Vec3 cameraPos, PoseStack poseStack, RenderProvider trail, DeltaTracker deltaTracker) implements TranslucentSubmit, BatchableSubmit {
        @Override
        public Object batchKey() {
            return TesselatorUtils.TRAIL;
        }

        @Override
        public float distanceToCameraSq() {
            return (float) trail.getRenderPosition(deltaTracker.getGameTimeDeltaPartialTick(false)).distanceToSqr(cameraPos);
        }

        @Override
        public FeatureRendererType<TrailSubmit> featureType() {
            return TrailFeatureRenderer.TYPE;
        }
    }
}