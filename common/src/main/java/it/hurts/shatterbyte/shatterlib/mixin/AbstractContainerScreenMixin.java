package it.hurts.shatterbyte.shatterlib.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
//    @Shadow protected int leftPos;
//
//    @Shadow protected int topPos;
//
//    @Inject(method = "renderContents", at = @At("RETURN"))
//    private void renderParticles(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
//        guiGraphics.pose().pushMatrix();
//        //guiGraphics.pose().translate(leftPos, topPos);
//        ParticleSystem.renderScreenParticles((Screen) (Object) this, guiGraphics, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
//        guiGraphics.pose().popMatrix();
//    }
}
