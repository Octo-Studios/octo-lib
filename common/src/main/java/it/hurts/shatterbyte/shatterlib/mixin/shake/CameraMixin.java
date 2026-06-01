package it.hurts.shatterbyte.shatterlib.mixin.shake;

import it.hurts.shatterbyte.shatterlib.module.camera_shake.CameraShakeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.Camera.class)
public abstract class CameraMixin {
    @Shadow
    private int matrixPropertiesDirty;

    @Shadow
    protected abstract void move(float forwards, float up, float right);

    @Shadow
    public abstract Quaternionf rotation();

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void shatterlib$applyShake(CameraRenderState cameraState, float partialTicks, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;

        if (player == null)
            return;

        var shakeRotation = new Vector3f();
        var shakeOffset = new Vector3f();

        for (var effect : CameraShakeManager.SHAKES.values()) {
            shakeRotation.add(effect.getShakeRotation(player, partialTicks));
            shakeOffset.add(effect.getShakeOffset(player, partialTicks));
        }

        var inverseRotation = new Quaternionf(this.rotation()).conjugate();

        var changed = false;

        if (shakeRotation.lengthSquared() > 0) {
            shakeRotation.rotate(inverseRotation);

            float rotationFactor = 10F;

            float rotationX = shakeRotation.x() * rotationFactor;
            float rotationY = shakeRotation.y() * rotationFactor;
            float rotationZ = shakeRotation.z() * rotationFactor;

            this.rotation().mul(new Quaternionf()
                    .rotateX(-rotationX * (float) (Math.PI / 180F))
                    .rotateY(-rotationY * (float) (Math.PI / 180F))
                    .rotateZ(-rotationZ * (float) (Math.PI / 180F)));

            changed = true;
        }

        if (shakeOffset.lengthSquared() > 0) {
            shakeOffset.rotate(inverseRotation);

            this.move(-shakeOffset.z(), shakeOffset.y(), shakeOffset.x());

            changed = true;
        }

        if (changed)
            this.matrixPropertiesDirty |= 3;
    }
}
