package it.hurts.shatterbyte.shatterlib.client.config;

import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.shatterlib.client.config.widget.ConfigButton;
import it.hurts.shatterbyte.shatterlib.client.config.widget.GenericObjectWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.ScrollableWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleConfigScreen extends ConfigScreen {
    ScrollableWidget configButtons;
    Map<ShatterConfig, GenericObjectWidget> cache = new HashMap<>();

    public MultipleConfigScreen(String modId, Screen prevScreen) {
        super(null, prevScreen);

        int y = 4;
        this.configButtons = new ScrollableWidget(0, 32, 128, this.height-32);
        for (ShatterConfig config : ConfigManager.getConfigsForMod(modId)) {
            ConfigButton button = new ConfigButton(config, 4, y + 32, 120, 24, () -> this.changeConfig(config));
            button.setParent(configButtons);
            configButtons.children().add(button);
            y += 4 + button.getHeight();
        }
        this.addRenderableWidget(configButtons);

        this.changeConfig(ConfigManager.getFirstForMod(modId));
    }

    private void changeConfig(ShatterConfig config) {
        if (this.config != null) {
            this.config.save(Platform.getConfigFolder());
        }

        this.config = config;

        this.removeWidget(this.scrollingObject);
        this.object = cache.computeIfAbsent(config, conf -> new GenericObjectWidget(
                        conf,
                        null,
                        new Annotation[]{},
                        null,
                        null,
                        () -> conf,
                        c -> {}
                )
        );
        this.scrollingObject = new ScrollableWidget(128, 32, this.width, this.height-32, object);
        this.addRenderableWidget(scrollingObject);

        repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (configButtons != null) {
            configButtons.setHeight(this.height-32);
            int y = configButtons.children().getLast().getY() + configButtons.children().getLast().getHeight() + 4;
            configButtons.maxScrollY = Math.max(0, y - configButtons.getHeight());
            configButtons.clamp();
        }

        scrollingObject.setWidth(this.width - 128);
        scrollingObject.setX(128);
        scrollingObject.setY(32);
        scrollingObject.setHeight(this.height-32);
        object.setWidth(scrollingObject.getWidth());
        object.repositionElements();
        scrollingObject.maxScrollY = Math.max(0, object.getHeight() - scrollingObject.getHeight());
        object.clamp(-scrollingObject.maxScrollY, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.vLine(128, 0, this.height, 0xff1c1c17);
        //guiGraphics.vLine(129, 0, this.height, 0xff1c1c17);
    }
}
