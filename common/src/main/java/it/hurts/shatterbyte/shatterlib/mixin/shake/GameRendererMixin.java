package it.hurts.shatterbyte.shatterlib.mixin.shake;

import it.hurts.shatterbyte.shatterlib.module.camera_shake.CameraShake;
import it.hurts.shatterbyte.shatterlib.module.camera_shake.CameraShakeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        var player = Minecraft.getInstance().player;

        if (player == null)
            return;

        Iterator<CameraShake> iterator = CameraShakeManager.SHAKES.values().iterator();

        while (iterator.hasNext()) {
            CameraShake effect = iterator.next();

            effect.update(player);

            if (effect.isFinished())
                iterator.remove();
        }
    }
}
