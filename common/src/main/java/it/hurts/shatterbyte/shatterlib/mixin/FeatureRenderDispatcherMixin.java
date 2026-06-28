package it.hurts.shatterbyte.shatterlib.mixin;

import it.hurts.shatterbyte.shatterlib.module.particle.trail.TrailFeatureRenderer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRendererMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.class)
public class FeatureRenderDispatcherMixin {
    @Shadow
    @Final
    private FeatureRendererMap featureRenderers;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void registerTrailRenderer(CallbackInfo ci) {
        if (this.featureRenderers.get(TrailFeatureRenderer.TYPE) == null) {
            this.featureRenderers.put(TrailFeatureRenderer.TYPE, new TrailFeatureRenderer());
        }
    }
}
