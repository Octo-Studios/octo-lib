package it.hurts.shatterbyte.shatterlib.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.shatterbyte.shatterlib.util.CommonCode;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class TrailRenderMixin {
    @Inject(method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;renderGroup(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void injectTrailRender(GpuBufferSlice gpuBufferSlice,
                                   LevelRenderState levelRenderState,
                                   ProfilerFiller profilerFiller,
                                   Matrix4f matrix4f,
                                   ResourceHandle resourceHandle,
                                   ResourceHandle resourceHandle2,
                                   boolean bl,
                                   Frustum frustum,
                                   ResourceHandle resourceHandle3,
                                   ResourceHandle resourceHandle4,
                                   CallbackInfo ci,
                                   @Local PoseStack poseStack
    ) {
        CommonCode.renderTrails(levelRenderState.cameraRenderState.pos, Minecraft.getInstance().renderBuffers().bufferSource(), poseStack, Minecraft.getInstance().getDeltaTracker());
    }
}
