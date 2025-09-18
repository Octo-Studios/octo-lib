package it.hurts.shatterbyte.shatterlib.fabric.mixin;

import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
public abstract class TrailRenderMixin {
//
//    @Shadow
//    @Final
//    private RenderBuffers renderBuffers;
//
//    @Shadow
//    protected abstract void checkPoseStack(PoseStack poseStack);
//
//    // net.minecraft.client.renderer.RenderBuffers.bufferSource
//    // Lnet/minecraft/client/renderer/RenderBuffers;bufferSource()Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;
//    @Inject(method = "renderLevel",
//            at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/client/renderer/LevelRenderer;addParticlesPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/Camera;FLcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", shift = At.Shift.AFTER))
//    public void render(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, Matrix4f frustumMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
//        Vec3 vec3 = camera.getPosition();
//        double d = vec3.x();
//        double e = vec3.y();
//        double g = vec3.z();
//
//        MultiBufferSource.BufferSource bufferSource = renderBuffers.bufferSource();
//        VertexConsumer consumer = bufferSource.getBuffer(TesselatorUtils.TRAIL_RENDER_TYPE);
//        PoseStack poseStack = new PoseStack();
//        float f = deltaTracker.getGameTimeDeltaPartialTick(false);
//
//        var player = Minecraft.getInstance().player;
//        for (RenderProvider trail : ShatterRenderManager.getProviders()) {
//            if (player == null || player.getPosition(f).subtract(trail.getRenderPosition(f)).length() > trail.getRenderDistance()) {
//                checkPoseStack(poseStack);
//            }
//            var position = trail.getRenderPosition(f);
//            poseStack.pushPose();
//            poseStack.translate(position.x - d, position.y - e, position.z - g);
//            trail.render(f, poseStack, consumer);
//            poseStack.popPose();
//        }
//
//        bufferSource.endBatch(TesselatorUtils.TRAIL_RENDER_TYPE);
//    }
}
