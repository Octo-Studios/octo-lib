package it.hurts.shatterbyte.shatterlib.client.config;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.config.widget.CheckboxWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestConfigScreen extends Screen {
    Screen prevScreen;

    public TestConfigScreen(Screen prevScreen) {
        super(Component.empty());
        this.prevScreen = prevScreen;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new CheckboxWidget(20, 20, Component.empty(), ShatterLib.CONFIG.getTestBoolean()));
        this.addRenderableWidget(new CheckboxWidget(20, 40, Component.empty(), ShatterLib.CONFIG.getAnotherTestBoolean()));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(prevScreen);
    }
}
