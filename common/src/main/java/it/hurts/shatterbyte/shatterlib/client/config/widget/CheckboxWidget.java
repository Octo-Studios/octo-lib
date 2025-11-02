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

public class CheckboxWidget extends AbstractEntryWidget<Boolean> {
    public CheckboxWidget(Supplier<Boolean> getter, Consumer<Boolean> setter, int x, int y, int width, int height, Component name) {
        super(getter, setter, x, y, width, height, name);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight(), 0x88000000);

        if (this.getValue()) {
            RenderUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xff00ff00);
        } else {
            RenderUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xffff0000);
        }

        boolean value = false;

        long start = System.nanoTime();
        for (int i = 0; i < 50000; i++) {
            value = this.getValue();
        }
        long end = System.nanoTime();
        
        guiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(end - start) + value, this.getX() + this.getWidth(), this.getY(), 0xffffffff, true);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        super.onClick(event, isDoubleClick);
        this.setValue(!this.getValue());
    }
}
