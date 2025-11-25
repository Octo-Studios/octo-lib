package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CheckboxWidget extends AbstractEntryWidget<Boolean> {
    public CheckboxWidget(ShatterConfig config, String fieldName, Boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(config, fieldName, defaultValue, getter, setter, 0, 0, 16, 16);
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
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        super.onClick(event, isDoubleClick);
        this.setValue(!this.getValue());
    }
}
