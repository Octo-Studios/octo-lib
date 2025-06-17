package it.hurts.octostudios.octolib.client.screen.widget;

import it.hurts.octostudios.octolib.client.animation.Tween;
import it.hurts.octostudios.octolib.util.OctoColor;
import it.hurts.octostudios.octolib.util.RenderUtils;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

public class TestPin extends AbstractWidget implements Child<TestGear> {
    @Setter
    OctoColor color = OctoColor.GREEN;
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
        if (this.isHovered() && this.color == OctoColor.GREEN) {
            this.color = OctoColor.BLUE;
        } else if (!this.isHovered() && this.color == OctoColor.BLUE) {
            this.color = OctoColor.GREEN;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0);
        guiGraphics.renderOutline(0, 0, this.width, this.height, color.getARGB());
        guiGraphics.pose().popPose();

        if (this.getParent() instanceof HasRenderMatrix has) {
            guiGraphics.pose().pushPose();
            Vec2 bleh = RenderUtils.toScreenCoords(has.getMatrix(), mouseX, mouseY);
            guiGraphics.pose().translate(bleh.x-0.5, bleh.y-0.5, 0);
            guiGraphics.renderOutline(0, 0, 2, 2, 0xffff0000);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        Tween tween = Tween.create();
        tween.tweenMethod(this::setColor, OctoColor.RED, OctoColor.GREEN, 0.5f);
        tween.start();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
