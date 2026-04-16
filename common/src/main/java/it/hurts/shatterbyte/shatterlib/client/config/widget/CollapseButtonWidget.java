package it.hurts.shatterbyte.shatterlib.client.config.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;

public class CollapseButtonWidget<E extends AbstractWidget> extends AbstractChildWidget<E> {
    private final Runnable onPress;

    public CollapseButtonWidget(Runnable onPress) {
        super(0, 0, 5, 5);
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xffff0000);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        this.onPress.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
