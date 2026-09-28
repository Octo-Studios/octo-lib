package it.hurts.shatterbyte.shatterlib.mixin;

import it.hurts.shatterbyte.shatterlib.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.shatterlib.util.DeltaTimeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "render", at = @At("HEAD"))
    private void injectHead(CallbackInfo ci) {
        DeltaTimeTracker.updateDeltaTime();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTick(CallbackInfo ci) {
        ShatterRenderManager.clientRenderTick();
    }
}
