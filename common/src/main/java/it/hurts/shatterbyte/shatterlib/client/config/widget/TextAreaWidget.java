package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.Minecart;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TextAreaWidget extends AbstractEntryWidget<String> {
    public TextAreaWidget(String defaultValue, Supplier<String> getter, Consumer<String> setter, int x, int y, int width, int height, String configPath, String fieldName) {
        super(defaultValue, getter, setter, x, y, width, height, configPath, fieldName);
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawString(Minecraft.getInstance().font, this.getValue(), 0, 0, 0xffffffff, true);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
