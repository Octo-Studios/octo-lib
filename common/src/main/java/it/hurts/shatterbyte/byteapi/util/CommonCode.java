package it.hurts.shatterbyte.byteapi.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.shatterbyte.byteapi.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.byteapi.module.particle.RenderProvider;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public class CommonCode {
    public static void applyShake(GuiGraphicsExtractor guiGraphics, float partialTick) {

    }

    public static void renderTrails(Vec3 cameraPos, MultiBufferSource.BufferSource consumers, PoseStack poseStack, DeltaTracker deltaTracker) {
        double d = cameraPos.x();
        double e = cameraPos.y();
        double g = cameraPos.z();

        VertexConsumer consumer = consumers.getBuffer(TesselatorUtils.TRAIL_RENDER_TYPE);
        float f = deltaTracker.getGameTimeDeltaPartialTick(false);

        for (RenderProvider trail : ShatterRenderManager.getProviders()) {
            Vec3 position = trail.getRenderPosition(f);
            poseStack.pushPose();
            poseStack.translate(position.x - d, position.y - e, position.z - g);
            trail.render(f, poseStack, consumer);
            poseStack.popPose();
        }

        consumers.endBatch(TesselatorUtils.TRAIL_RENDER_TYPE);
    }
}
