package it.hurts.octostudios.octolib.fabric.mixin;

import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.octolib.modules.particles.RenderProvider;
import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
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
    @Inject(method = "renderLevel",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;addParticlesPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/LightTexture;FLnet/minecraft/client/renderer/FogParameters;)V"))
    public void render(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        render(deltaTracker, bl, camera, gameRenderer, lightTexture, matrix4f, matrix4f2);
    }

    @Unique
    private void render(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2) {
        Vec3 vec3 = camera.getPosition();
        double d = vec3.x();
        double e = vec3.y();
        double g = vec3.z();

        MultiBufferSource.BufferSource bufferSource = renderBuffers.bufferSource();
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
