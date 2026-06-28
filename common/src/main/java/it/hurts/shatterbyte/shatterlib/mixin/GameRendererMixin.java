package it.hurts.shatterbyte.shatterlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.hurts.shatterbyte.shatterlib.client.particle.ParticleSystem;
import it.hurts.shatterbyte.shatterlib.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.shatterlib.util.DeltaTimeTracker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    private void injectHead(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        DeltaTimeTracker.updateDeltaTime();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isGameLoadFinished()Z"))
    private void renderTick(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        ShatterRenderManager.clientRenderTick();
    }

//    @Inject(require = 0, method = "render", at = @At(value = "INVOKE", target = "", shift = At.Shift.AFTER))
//    private void renderScreenParticles(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci, @Local GuiGraphicsExtractor guiGraphics) {
//        ParticleSystem.renderScreenParticles(minecraft.gui.screen(), guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
//    }
}
