package it.hurts.shatterbyte.shatterlib.client.config;

import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.shatterlib.client.config.widget.ConfigButton;
import it.hurts.shatterbyte.shatterlib.client.config.widget.GenericObjectWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.ScrollableWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.screens.Screen;

import java.lang.annotation.Annotation;

public class MultipleConfigScreen extends ConfigScreen {
    ScrollableWidget configButtons;

    public MultipleConfigScreen(String modId, Screen prevScreen) {
        super(ConfigManager.getFirstForMod(modId), prevScreen);

        int y = 4;
        this.configButtons = new ScrollableWidget(0, 0, 128, this.height);
        for (ShatterConfig config : ConfigManager.getConfigsForMod(modId)) {
            ConfigButton button = new ConfigButton(config, 4, y, 120, 20, () -> this.changeConfig(config));
            button.setParent(configButtons);
            configButtons.children().add(button);
            y += 24;
        }
        this.addRenderableWidget(configButtons);
    }

    private void changeConfig(ShatterConfig config) {
        if (this.config != null) {
            this.config.save(Platform.getConfigFolder());
        }

        this.config = config;

        this.removeWidget(this.scrollingObject);
        this.object = new GenericObjectWidget(config, null, new Annotation[]{}, null, null, () -> config, conf -> {});
        this.scrollingObject = new ScrollableWidget(128, 0, this.width, this.height, object);
        this.addRenderableWidget(scrollingObject);

        repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (configButtons != null) {
            configButtons.setHeight(this.height);
        }

        scrollingObject.setWidth(this.width - 128);
        scrollingObject.setX(128);
        scrollingObject.setHeight(this.height);
        object.setWidth(scrollingObject.getWidth());
        object.repositionElements();
        scrollingObject.maxScrollY = Math.max(0, object.getHeight() - this.height);
        object.clamp(-scrollingObject.maxScrollY, 0);
    }

}
