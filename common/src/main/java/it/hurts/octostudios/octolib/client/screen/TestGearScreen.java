package it.hurts.octostudios.octolib.client.screen;

import it.hurts.octostudios.octolib.client.screen.widget.TestGear;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestGearScreen extends Screen {
    public TestGearScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new TestGear((int) (this.width/2f-64), (int) (this.height/2f-64)));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
