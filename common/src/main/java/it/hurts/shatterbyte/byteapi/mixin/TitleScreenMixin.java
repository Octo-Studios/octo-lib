package it.hurts.shatterbyte.byteapi.mixin;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
//    @Inject(method = "init", at = @At("RETURN"))
//    private void injected(CallbackInfo ci) {
//        if (!Platform.isDevelopmentEnvironment()) {
//            return;
//        }
//
//        TitleScreen thisScreen = (TitleScreen) (Object) this;
//        addRenderableWidget(thisScreen, Button.builder(
//                Component.literal("Test Config"),
//                button -> Minecraft.getInstance().setScreen(new MultipleConfigScreen(ByteAPI.MOD_ID, thisScreen))
//        ).bounds(thisScreen.width / 2 - 50, 2, 100, 20).build());
//    }
}
