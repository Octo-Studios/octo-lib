package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.OctoLibClient;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Unique
    private long octolib$startNanos;

    @Inject(method = "runTick", at = @At(value = "HEAD"))
    private void injectStartNanos(boolean renderLevel, CallbackInfo ci) {
        octolib$startNanos = Util.getNanos();
    }

    @Inject(method = "runTick", at = @At(value = "TAIL"))
    private void injectDeltaNanos(boolean renderLevel, CallbackInfo ci) {
        OctoLibClient.DELTA_NANOS = Util.getNanos() - octolib$startNanos;
    }
}
