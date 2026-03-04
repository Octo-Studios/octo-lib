package it.hurts.shatterbyte.shatterlib.client.config;

import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.shatterlib.client.config.widget.ConfigButton;
import it.hurts.shatterbyte.shatterlib.client.config.widget.GenericObjectWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.ScrollableWidget;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class MultipleConfigScreen extends ConfigScreen {
    private static final int CANVAS_HORIZONTAL_PADDING = 4;
    private static final int SIDEBAR_WIDTH = 128;
    private static final int SIDEBAR_TILE_PADDING = 4;

    ScrollableWidget configButtons;
    Map<ShatterConfig, GenericObjectWidget> cache = new HashMap<>();

    public MultipleConfigScreen(String modId, Screen prevScreen) {
        super(null, prevScreen);

        int y = 4;
        this.configButtons = new ScrollableWidget(0, 32, SIDEBAR_WIDTH, this.height - 32);
        for (ShatterConfig config : ConfigManager.getConfigsForMod(modId)) {
            ConfigButton button = new ConfigButton(
                    config,
                    SIDEBAR_TILE_PADDING,
                    y,
                    120,
                    24,
                    () -> this.changeConfig(config),
                    () -> this.config == config
            );
            button.setParent(configButtons);
            configButtons.children().add(button);
            y += 4 + button.getHeight();
        }
        this.addRenderableWidget(configButtons);

        this.changeConfig(ConfigManager.getFirstForMod(modId));
    }

    private void changeConfig(ShatterConfig config) {
        if (config == null) {
            return;
        }

        if (this.config != null) {
            this.config.save(Platform.getConfigFolder());
        }

        this.config = config;

        if (this.scrollingObject != null) {
            this.removeWidget(this.scrollingObject);
        }

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
        this.scrollingObject = new ScrollableWidget(getContentLeft(), 32, this.width, this.height - 32, object);
        this.scrollingObject.setPinScrollbarToScreenRight(true);
        this.scrollingObject.setScrollbarRightInset(0);
        this.addRenderableWidget(scrollingObject);
        ensureSearchWidget();
        applySearchQuery();

        repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (configButtons != null) {
            configButtons.setHeight(this.height-32);
            int contentBottom = 0;
            for (AbstractWidget widget : configButtons.children()) {
                int localY;
                if (widget instanceof Child<?> child) {
                    localY = child.getLocalY();
                } else {
                    localY = widget.getY() - configButtons.getY();
                }

                contentBottom = Math.max(contentBottom, localY + widget.getHeight());
            }

            configButtons.maxScrollY = Math.max(0, contentBottom - configButtons.getHeight());
            configButtons.clamp();

            boolean hasScrollbar = configButtons.maxScrollY > 0;
            int scrollbarWidth = hasScrollbar ? configButtons.getScrollbarWidth() : 0;
            int tileWidth = Math.max(
                    1,
                    configButtons.getWidth() - SIDEBAR_TILE_PADDING * 2 - scrollbarWidth
            );

            for (AbstractWidget widget : configButtons.children()) {
                if (widget instanceof ConfigButton) {
                    widget.setWidth(tileWidth);
                }
            }
        }

        if (scrollingObject == null || object == null) {
            repositionSearchWidget();
            return;
        }

        scrollingObject.setWidth(Math.max(32, this.width - SIDEBAR_WIDTH - CANVAS_HORIZONTAL_PADDING * 2));
        scrollingObject.setX(getContentLeft());
        scrollingObject.setY(32);
        scrollingObject.setHeight(this.height - 32);
        object.setWidth(scrollingObject.getWidth());
        object.repositionElements();
        scrollingObject.maxScrollY = Math.max(0, object.getHeight() - scrollingObject.getHeight());
        object.clamp(-scrollingObject.maxScrollY, 0);
        repositionSearchWidget();
    }

    @Override
    protected int getContentLeft() {
        return SIDEBAR_WIDTH + CANVAS_HORIZONTAL_PADDING;
    }

    @Override
    protected int getContentRight() {
        return this.width - CANVAS_HORIZONTAL_PADDING;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.vLine(SIDEBAR_WIDTH, 0, this.height, 0xff1c1c17);
        guiGraphics.hLine(0, this.width, 31, 0xff1c1c17);
        //guiGraphics.vLine(129, 0, this.height, 0xff1c1c17);
    }
}
