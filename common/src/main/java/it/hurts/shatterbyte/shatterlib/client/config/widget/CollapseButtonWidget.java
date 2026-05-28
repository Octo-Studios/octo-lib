package it.hurts.shatterbyte.shatterlib.client.config.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.function.BooleanSupplier;

public class CollapseButtonWidget<E extends AbstractWidget> extends AbstractChildWidget<E> {
    private static final int BRANCH_LINE_COLOR = 0xff3c3c42;

    private final Runnable onPress;
    private final BooleanSupplier collapsedSupplier;

    public CollapseButtonWidget(Runnable onPress, BooleanSupplier collapsedSupplier) {
        super(0, 0, 5, 5);
        this.onPress = onPress;
        this.collapsedSupplier = collapsedSupplier;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        //guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, BRANCH_LINE_COLOR);
        guiGraphics.horizontalLine(this.getX()+1, this.getX()+3, this.getY()+2, 0xffffffff);

        if (collapsedSupplier.getAsBoolean()) {
            guiGraphics.verticalLine(this.getX()+2, this.getY(), this.getY()+4, 0xffffffff);
        }
    }

    public void renderExpandedBranchLine(GuiGraphicsExtractor guiGraphics) {
        E parent = this.getParent();
        if (parent == null) {
            return;
        }

        int x = this.getX() + this.width / 2;
        int top = this.getY() + this.height;
        int bottom = parent.getY() + parent.getHeight() - 1;
        if (bottom > top) {
            guiGraphics.verticalLine(x, top, bottom, BRANCH_LINE_COLOR);
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        this.onPress.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
