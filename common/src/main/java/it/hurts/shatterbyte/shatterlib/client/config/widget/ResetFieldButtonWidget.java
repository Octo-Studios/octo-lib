package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;

public class ResetFieldButtonWidget extends AbstractFieldElementWidget {
    private final Runnable reset;

    public ResetFieldButtonWidget(AbstractEntryWidget<?> entry) {
        super(0, 0, 16, 16);
        this.reset = entry::resetValue;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight(), 0x6633ffff);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        reset.run();
    }
}
