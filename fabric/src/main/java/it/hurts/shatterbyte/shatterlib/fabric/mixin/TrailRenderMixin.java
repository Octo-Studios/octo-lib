package it.hurts.shatterbyte.shatterlib.fabric.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import it.hurts.shatterbyte.shatterlib.util.CommonCode;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class TrailRenderMixin {
    @Inject(method = "", at = @At(value = "INVOKE", target = "", shift = At.Shift.AFTER))
    private void injectTrailRender(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, Matrix4f frustumMatrix, Matrix4f projectionMatrix, Matrix4f cullingProjectionMatrix, GpuBufferSlice shaderFog, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
        CommonCode.renderTrails(camera, Minecraft.getInstance().renderBuffers().bufferSource(), context.matrixStack(), context.tickCounter());
    }
}
