package it.hurts.shatterbyte.shatterlib.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.shatterbyte.shatterlib.module.particle.RenderProvider;
import it.hurts.shatterbyte.shatterlib.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.shatterlib.module.particle.trail.TrailFeatureRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.phase.TranslucentFeatureRenderPhase;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    @Final
    private SubmitNodeStorage submitNodeStorage;

    @Inject(method = "submitFeatures", at = @At("TAIL"))
    private void injectTrailSubmits(LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector, boolean renderOutline, CallbackInfo ci) {
        Vec3 cameraPos = levelRenderState.cameraRenderState.pos;
        PoseStack poseStack = new PoseStack();
        DeltaTracker deltaTracker = Minecraft.getInstance().getDeltaTracker();

        SubmitNodeCollection collection = this.submitNodeStorage.order(0);
        TranslucentFeatureRenderPhase phase = collection.translucentModels;

        for (RenderProvider<?, ?> trail : ShatterRenderManager.getProviders()) {
            phase.submit(new TrailFeatureRenderer.TrailSubmit(cameraPos, poseStack, trail, deltaTracker));
        }
    }
}
