package it.hurts.shatterbyte.shatterlib.mixin;

import it.hurts.shatterbyte.shatterlib.client.animation.TweenSystem;
import it.hurts.shatterbyte.shatterlib.client.particle.ParticleSystem;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "runTick", at = @At(value = "TAIL"))
    private void injectDeltaNanos(boolean renderLevel, CallbackInfo ci) {
        TweenSystem.RenderThreadExecutor.executeAll();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;tick(Z)V"))
    private void tick(CallbackInfo ci) {
        ParticleSystem.tick();
    }
}
