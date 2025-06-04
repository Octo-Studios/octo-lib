package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.client.shake.Shakeable;
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
//    @Unique
//    private Vector2f shakeOffset = new Vector2f(0, 0);
//
//    @Override
//    public Vector2f getShakeOffset() {
//        return shakeOffset;
//    }
//
//    @Override
//    public void setShakeOffset(Vector2f offset) {
//        this.shakeOffset = offset;
//    }
//
//    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderBackground(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
//            shift = At.Shift.AFTER))
//    private void beforeRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
//        guiGraphics.pose().pushPose();
//        guiGraphics.pose().translate(shakeOffset.x(), shakeOffset.y(), 0);
//    }
//
//    @Inject(method = "render", at = @At("RETURN"))
//    private void afterRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
//        guiGraphics.pose().popPose();
//    }
}
