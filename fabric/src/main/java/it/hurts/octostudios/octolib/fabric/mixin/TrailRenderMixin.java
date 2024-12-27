package it.hurts.octostudios.octolib.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.octolib.modules.particles.RenderProvider;
import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.*;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class TrailRenderMixin {

    @Unique
    private void checkPoseStack(PoseStack poseStack) {
        if (!poseStack.clear()) {
            throw new IllegalStateException("Pose stack not empty");
        }
    }
    
    // net.minecraft.client.renderer.RenderBuffers.bufferSource
    // Lnet/minecraft/client/renderer/RenderBuffers;bufferSource()Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;
    @Inject(method = "render",
            at = @At(value = "HEAD"))
    public void render(LightTexture lightTexture, Camera camera, float f, CallbackInfo ci) {
        render(Minecraft.getInstance().getDeltaTracker(), camera);
    }

    @Unique
    private void render(DeltaTracker deltaTracker, Camera camera) {
        Vec3 vec3 = camera.getPosition();
        double d = vec3.x();
        double e = vec3.y();
        double g = vec3.z();

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack poseStack = new PoseStack();
        float f = deltaTracker.getGameTimeDeltaPartialTick(false);

        var player = Minecraft.getInstance().player;
        for (RenderProvider trail : OctoRenderManager.getProviders()) {
            if (player == null || player.getPosition(f).subtract(trail.getRenderPosition(f)).length() > trail.getRenderDistance())
                checkPoseStack(poseStack);
            var position = trail.getRenderPosition(f);
            poseStack.pushPose();
            poseStack.translate(position.x - d, position.y - e, position.z - g);
            trail.render(f, poseStack, bufferSource);
            poseStack.popPose();
        }
        bufferSource.endBatch();
    }
}
