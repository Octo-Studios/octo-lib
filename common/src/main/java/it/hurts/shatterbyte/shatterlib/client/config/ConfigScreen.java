package it.hurts.shatterbyte.shatterlib.client.config;

import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.config.widget.GenericObjectWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.ScrollableWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.lang.annotation.Annotation;

public class ConfigScreen extends Screen {

    public static final Atlas ATLAS = new Atlas(Identifier.fromNamespaceAndPath(ShatterLib.MOD_ID, "textures/config/config_atlas.png"), 263, 76);

    ShatterConfig config;
    Screen prevScreen;
    GenericObjectWidget object;
    ScrollableWidget scrollingObject;

    public ConfigScreen(ShatterConfig config, Screen prevScreen) {
        super(Component.empty());
        this.prevScreen = prevScreen;

        if (config != null) {
            this.config = config;

            object = new GenericObjectWidget(config, null, new Annotation[]{}, null, null, () -> config, conf -> {
            });
            scrollingObject = new ScrollableWidget(0, 32, this.width, this.height-32, object);
            this.addRenderableWidget(scrollingObject);

            repositionElements();
        }
    }

    @Override
    protected void init() {
        super.init();
        this.repositionElements();

    }

    @Override
    protected void repositionElements() {
        scrollingObject.setWidth(this.width);
        //scrollingObject.setX(128);
        scrollingObject.setHeight(this.height-32);
        object.setWidth(scrollingObject.getWidth());
        object.repositionElements();
        scrollingObject.maxScrollY = Math.max(0, object.getHeight() - this.height);
        object.clamp(-scrollingObject.maxScrollY, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        //guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ATLAS.location, 0, 0, 0, 0, 263, 76, 263, 76);
        //UIElements.TEST.render(guiGraphics, RenderPipelines.GUI_TEXTURED, 0, 0, mouseX, mouseY);
        //UIElements.SLIDER_THINGY.render(guiGraphics, RenderPipelines.GUI_TEXTURED, mouseX, mouseY);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, width, height,0xFF2C2B31);
        guiGraphics.fillGradient(0, (int) (this.height*0.75f), this.width, this.height, 0xFF2C2B31, 0xFF222226);
    }

    @Override
    public void onClose() {
        config.save(Platform.getConfigFolder());
        this.minecraft.setScreen(prevScreen);
    }
}
