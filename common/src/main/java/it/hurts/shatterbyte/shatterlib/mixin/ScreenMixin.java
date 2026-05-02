package it.hurts.shatterbyte.shatterlib.mixin;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow
    public int height;

    //    @Inject(method = "render", at = @At("RETURN"))
//    private void beforeRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
//        Screen screen = (Screen) (Object) this;
//        if (screen instanceof AbstractContainerScreen<?>) {
//            return;
//        }
//
//        ParticleSystem.renderScreenParticles(screen, guiGraphics, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
//    }
//    @Inject(method = "render", at = @At("TAIL"))
//    private void injectMousePos(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
//        if (!Platform.isDevelopmentEnvironment()) {
//            return;
//        }
//
//        Font font = Minecraft.getInstance().font;
//        String string = "x: "+mouseX+", y: "+mouseY;
//        guiGraphics.fill(0, this.height-13, font.width(string)+3, this.height, 0xff000000);
//        guiGraphics.drawString(font, string, 2, this.height-11, 0xffffffff, true);
//    }
}
