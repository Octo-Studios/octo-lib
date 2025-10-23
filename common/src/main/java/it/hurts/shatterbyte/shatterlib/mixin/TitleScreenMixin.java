package it.hurts.shatterbyte.shatterlib.mixin;

import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.shatterlib.client.config.TestConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.architectury.hooks.client.screen.ScreenHooks.addRenderableWidget;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At("RETURN"))
    private void injected(CallbackInfo ci) {
        if (!Platform.isDevelopmentEnvironment()) {
            return;
        }

        TitleScreen thisScreen = (TitleScreen) (Object) this;
        addRenderableWidget(thisScreen, Button.builder(
                Component.literal("Test Config"),
                button -> Minecraft.getInstance().setScreen(new TestConfigScreen(thisScreen))
        ).bounds(thisScreen.width / 2 - 50, 2, 100, 20).build());
    }
}
