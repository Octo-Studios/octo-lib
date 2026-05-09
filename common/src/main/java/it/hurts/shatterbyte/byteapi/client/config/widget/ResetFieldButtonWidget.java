package it.hurts.shatterbyte.byteapi.client.config.widget;

import it.hurts.shatterbyte.byteapi.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.byteapi.client.config.UIElements;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.function.Supplier;

public class ResetFieldButtonWidget extends IconButtonWidget<FieldWidget> {
    Supplier<Boolean> isEqualToDefault;

    public ResetFieldButtonWidget(AbstractEntryWidget<?> entry) {
        super(0, 0, 13, 14, entry::resetValue, UIElements.ICON_RESET);
        this.isEqualToDefault = entry::isAtDefaultValue;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.active = !isEqualToDefault.get();
        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }
}
