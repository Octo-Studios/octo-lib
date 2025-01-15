package it.hurts.octostudios.octolib.quilt.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import it.hurts.octostudios.octolib.modules.particles.RenderProvider;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class TrailRenderMixin {

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    protected abstract void checkPoseStack(PoseStack poseStack);

    // net.minecraft.client.renderer.RenderBuffers.bufferSource
    // Lnet/minecraft/client/renderer/RenderBuffers;bufferSource()Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;
    @Inject(method = "renderLevel", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V"))
    public void renderLevel(PoseStack poseStack, float partialTicks, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        render(bl, camera, gameRenderer, lightTexture, poseStack, partialTicks);
    }


    @Unique
    private void render(boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, PoseStack poseStack, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        double camX = cameraPos.x();
        double camY = cameraPos.y();
        double camZ = cameraPos.z();

        MultiBufferSource.BufferSource bufferSource = renderBuffers.bufferSource();
        float f = Minecraft.getInstance().getDeltaFrameTime();

        var player = Minecraft.getInstance().player;
        for (RenderProvider trail : OctoRenderManager.getProviders()) {
            if (player == null || player.getPosition(f).subtract(trail.getRenderPosition(f)).length()
                    > trail.getRenderDistance())
                checkPoseStack(poseStack);
            var position = trail.getRenderPosition(f);
            poseStack.pushPose();
            poseStack.translate(position.x - camX, position.y - camY, position.z - camZ);
            trail.render(f, poseStack, bufferSource);
            poseStack.popPose();
        }
        bufferSource.endBatch();
    }
}
