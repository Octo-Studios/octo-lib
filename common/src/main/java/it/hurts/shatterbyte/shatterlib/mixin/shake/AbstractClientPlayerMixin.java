package it.hurts.shatterbyte.shatterlib.mixin.shake;

import it.hurts.shatterbyte.shatterlib.module.camera_shake.CameraShakeManager;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
    @Inject(method = "getFieldOfViewModifier(ZF)F", at = @At("RETURN"), cancellable = true)
    public void getFieldOfViewModifier(boolean firstPerson, float effectScale, CallbackInfoReturnable<Float> cir) {
        var modifier = 0F;

        for (var effect : CameraShakeManager.SHAKES.values())
            modifier += effect.getShakeFOV((AbstractClientPlayer) (Object) this, effectScale);

        if (modifier != 0F)
            cir.setReturnValue(cir.getReturnValue() + modifier);
    }
}
