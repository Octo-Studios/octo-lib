package it.hurts.shatterbyte.shatterlib.mixin;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public class ScreenMixin {
//    @Inject(method = "render", at = @At("RETURN"))
//    private void beforeRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
//        Screen screen = (Screen) (Object) this;
//        if (screen instanceof AbstractContainerScreen<?>) {
//            return;
//        }
//
//        ParticleSystem.renderScreenParticles(screen, guiGraphics, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
//    }
}
