package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CheckboxWidget extends AbstractEntryWidget<Boolean> {
    public CheckboxWidget(Boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter, int x, int y, int width, int height, String configPath, String fieldName) {
        super(defaultValue, getter, setter, x, y, width, height, configPath, fieldName);
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.getWidth(), this.getHeight(), 0x88000000);

        if (this.getValue()) {
            RenderUtils.renderOutline(guiGraphics, 0, 0, this.getWidth(), this.getHeight(), 0xff00ff00);
        } else {
            RenderUtils.renderOutline(guiGraphics, 0, 0, this.getWidth(), this.getHeight(), 0xffff0000);
        }
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
