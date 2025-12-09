package it.hurts.shatterbyte.shatterlib.client.config;

import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.config.widget.DynamicallySized;
import it.hurts.shatterbyte.shatterlib.client.config.widget.GenericObjectWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ConfigScreen extends Screen {
    public static final Atlas ATLAS = new Atlas(ResourceLocation.fromNamespaceAndPath(ShatterLib.MOD_ID, "textures/config/config_atlas.png"), 263, 76);
    ShatterConfig config;
    Screen prevScreen;
    GenericObjectWidget object;

    public ConfigScreen(ShatterConfig config, Screen prevScreen) {
        super(Component.literal(config.getName()));
        this.config = config;
        this.prevScreen = prevScreen;

        object = new GenericObjectWidget(config, null, null, () -> config, conf -> {});
        this.addRenderableWidget(object);

        repositionElements();
    }

    @Override
    protected void init() {
        super.init();

    }

    @Override
    protected void repositionElements() {
        object.setWidth(this.width);
        object.repositionElements();
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
    }

    @Override
    public void onClose() {
        config.save(Platform.getConfigFolder());
        this.minecraft.setScreen(prevScreen);
    }
}
