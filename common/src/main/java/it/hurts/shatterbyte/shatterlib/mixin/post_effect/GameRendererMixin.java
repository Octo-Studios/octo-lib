package it.hurts.shatterbyte.shatterlib.mixin.post_effect;

import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import it.hurts.shatterbyte.shatterlib.module.post_effect.RenderStage;
import it.hurts.shatterbyte.shatterlib.module.post_effect.init.ShatterLibPostEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Final
    @Shadow
    Minecraft minecraft;

    @Final
    @Shadow
    private CrossFrameResourcePool resourcePool;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V", shift = At.Shift.AFTER))
    private void shatterlib$onRenderLevel(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        this.shatterlib$renderPostEffects(RenderStage.LEVEL);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void shatterlib$onRenderScreen(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        this.shatterlib$renderPostEffects(RenderStage.SCREEN);
    }

    @Unique
    private void shatterlib$renderPostEffects(RenderStage stage) {
        if (minecraft == null)
            return;

        for (var postEffect : ShatterLibPostEffects.getPostEffects().values()) {
            if (postEffect.getStage() != stage || !postEffect.shouldRender())
                continue;

            var postChain = minecraft.getShaderManager().getPostChain(postEffect.getPath(), Set.of(PostChain.MAIN_TARGET_ID));

            if (postChain == null)
                continue;

            postEffect.construct(postChain);

            postChain.process(minecraft.getMainRenderTarget(), resourcePool);
        }
    }
}
