package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.module.config.type.SimpleEntry;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class CheckboxWidget extends AbstractEntryWidget<SimpleEntry<Boolean>> {
    public CheckboxWidget(int x, int y, Component name, SimpleEntry<Boolean> entry) {
        super(x, y, 16, 16, name, entry);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight(), 0x88000000);

        if (this.getEntry().getValue()) {
            RenderUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xff00ff00);
        } else {
            RenderUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xffff0000);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        super.onClick(event, isDoubleClick);
        this.getEntry().setValue(!this.getEntry().getValue());
    }
}
