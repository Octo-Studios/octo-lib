package it.hurts.octostudios.octolib.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.octostudios.octolib.module.particle.OctoRenderManager;
import it.hurts.octostudios.octolib.module.particle.RenderProvider;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CommonCode {
    public static void applyShake(GuiGraphics guiGraphics, float partialTick) {

    }

    public static void renderTrails(Camera camera, MultiBufferSource.BufferSource consumers, PoseStack poseStack, DeltaTracker deltaTracker) {
        Vec3 vec3 = camera.getPosition();
        double d = vec3.x();
        double e = vec3.y();
        double g = vec3.z();

        VertexConsumer consumer = consumers.getBuffer(TesselatorUtils.TRAIL_RENDER_TYPE);
        float f = deltaTracker.getGameTimeDeltaPartialTick(false);

        for (RenderProvider trail : OctoRenderManager.getProviders()) {
            Vec3 position = trail.getRenderPosition(f);
            poseStack.pushPose();
            poseStack.translate(position.x - d, position.y - e, position.z - g);
            trail.render(f, poseStack, consumer);
            poseStack.popPose();
        }

        consumers.endBatch(TesselatorUtils.TRAIL_RENDER_TYPE);
    }
}
