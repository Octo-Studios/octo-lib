package it.hurts.octostudios.octolib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.hurts.octostudios.octolib.client.particle.ParticleSystem;
import it.hurts.octostudios.octolib.module.particle.OctoRenderManager;
import it.hurts.octostudios.octolib.util.DeltaTimeTracker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;
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
        OctoRenderManager.clientRenderTick();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    private void renderScreenParticles(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci, @Local GuiGraphics guiGraphics) {
        ParticleSystem.renderScreenParticles(minecraft.screen, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }
}
