package it.hurts.shatterbyte.shatterlib.client.screen.widget;

import it.hurts.shatterbyte.shatterlib.client.animation.Tween;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

public class TestPin extends AbstractWidget implements Child<TestGear> {
    @Setter
    ShatterColor color = ShatterColor.GREEN;
    TestGear parent;

    public TestPin(int x, int y, TestGear parent) {
        super(x, y, 24, 24, Component.empty());
        this.setParent(parent);
        this.setPosition(x, y);
    }

    @Override
    public @Nullable TestGear getParent() {
        return parent;
    }

    @Override
    public void setParent(@Nullable TestGear parent) {
        this.parent = parent;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHovered() && this.color == ShatterColor.GREEN) {
            this.color = ShatterColor.BLUE;
        } else if (!this.isHovered() && this.color == ShatterColor.BLUE) {
            this.color = ShatterColor.GREEN;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.getX(), this.getY());
        guiGraphics.renderOutline(0, 0, this.width, this.height, color.getARGB());
        guiGraphics.pose().popMatrix();

        if (this.getParent() instanceof HasRenderMatrix has) {
            guiGraphics.pose().pushMatrix();
            Vec2 bleh = RenderUtils.toScreenCoords(has.getMatrix(), mouseX, mouseY);
            guiGraphics.pose().translate((float) (bleh.x-0.5), (float) (bleh.y-0.5));
            guiGraphics.renderOutline(0, 0, 2, 2, 0xffff0000);
            guiGraphics.pose().popMatrix();
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        Tween tween = Tween.create();
        tween.tweenMethod(this::setColor, ShatterColor.RED, ShatterColor.GREEN, 0.5f);
        tween.start();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
