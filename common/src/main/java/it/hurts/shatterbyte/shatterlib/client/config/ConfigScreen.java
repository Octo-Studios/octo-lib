package it.hurts.shatterbyte.shatterlib.client.config;

import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.shatterlib.client.config.widget.GenericObjectWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
        GenericObjectWidget configWidget = new GenericObjectWidget(config, null, null, () -> config, config -> {});
        configWidget.setWidth(this.width);
        configWidget.repositionWidgets();
        this.addRenderableWidget(configWidget);
    }

    @Override
    public void onClose() {
        config.save(Platform.getConfigFolder());
        this.minecraft.setScreen(prevScreen);
    }
}
