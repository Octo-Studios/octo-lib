package it.hurts.octostudios.octolib.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.octostudios.octolib.client.particle.ParticleSystem;
import it.hurts.octostudios.octolib.client.shake.Shakeable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void beforeRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        ParticleSystem.renderScreenParticles((Screen) (Object) this, guiGraphics, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
    }
}
