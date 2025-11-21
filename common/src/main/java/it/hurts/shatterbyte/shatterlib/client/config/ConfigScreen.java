package it.hurts.shatterbyte.shatterlib.client.config;

import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConfigScreen extends Screen {
    ShatterConfig config;
    Screen prevScreen;

    public ConfigScreen(ShatterConfig config, Screen prevScreen) {
        super(Component.literal(config.getName()));
        this.config = config;
        this.prevScreen = prevScreen;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void onClose() {
        config.save(Platform.getConfigFolder());
        this.minecraft.setScreen(prevScreen);
    }
}
