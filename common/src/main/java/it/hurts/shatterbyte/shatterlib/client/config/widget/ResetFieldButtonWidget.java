package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class ResetFieldButtonWidget extends IconButtonWidget<FieldWidget> {
    Supplier<Boolean> isEqualToDefault;

    public ResetFieldButtonWidget(AbstractEntryWidget<?> entry) {
        super(0, 0, 13, 14, entry::resetValue, UIElements.ICON_RESET);
        this.isEqualToDefault = entry::isAtDefaultValue;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.active = !isEqualToDefault.get();
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }
}
