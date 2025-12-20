package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.Minecart;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TextAreaWidget extends AbstractEntryWidget<String> {
    Font font = Minecraft.getInstance().font;

    public TextAreaWidget(ShatterConfig config, PathContainerWidget parent, String defaultValue, Supplier<String> getter, Consumer<String> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 200, 14);
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        UIElements.TEXT_AREA.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        guiGraphics.drawString(Minecraft.getInstance().font, this.getValue(), this.getX()+4, this.getY()+4, 0xffcccccc, true);
    }
}
