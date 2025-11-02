package it.hurts.shatterbyte.shatterlib.client.config;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.config.widget.CheckboxWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;

public class TestConfigScreen extends Screen {
    Screen prevScreen;

    public TestConfigScreen(Screen prevScreen) {
        super(Component.empty());
        this.prevScreen = prevScreen;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new CheckboxWidget(ShatterLib.CONFIG::isTestBool, ShatterLib.CONFIG::setTestBool, 20, 40, 16, 16, Component.empty()));
    }

    @Override
    public void onClose() {
        ShatterLib.CONFIG.save(Path.of("."));
        this.minecraft.setScreen(prevScreen);
    }
}
