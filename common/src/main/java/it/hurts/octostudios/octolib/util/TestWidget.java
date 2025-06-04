package it.hurts.octostudios.octolib.util;

import it.hurts.octostudios.octolib.OctoLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TestWidget extends AbstractWidget {
    private double cX;
    private double cY;
    public Easing easing;
    public Animator animator;
    private Animator hoverAnimator;

    public TestWidget(int x, int y, Easing easing) {
        super(x, y, 8, 8, Component.literal(easing.name()));
        this.easing = easing;
        this.hoverAnimator = new Animator(Easing.EASE_IN_OUT_CUBIC, 0, -3, 0.25, this::setCurrentY);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float partialTick) {
        float actualPartialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(cX + this.getX(), cY + this.getY(), 0);
        guiGraphics.renderOutline(0, 0, this.getWidth(), this.getHeight(), 0xffffffff);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0);
        guiGraphics.renderOutline(-2, -12, 112, this.getHeight() + 14, 0x50ffffff);
        guiGraphics.pose().scale(0.75f, 1, 1);
        guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), 0, -10, 0x50ffffff, true);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void setCurrentX(double x) {
        this.cX = x;
    }

    public void setCurrentY(double y) {
        this.cY = y;
    }

    @Override
    public boolean isHovered() {
        boolean hovered = super.isHovered();

        if (hovered && !this.hoverAnimator.isRunning()) {
            //this.hoverAnimator.start();
        }

        return hovered;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (animator != null) {
            this.setCurrentX(0);
            this.setCurrentY(0);
            animator.stop();
        }

        this.animator = new Animator(easing, 0, 100, 0.75, this::setCurrentX, () -> OctoLib.LOGGER.info("test callback"))
                .sleep(0.25)
                .then(new Animator(easing, 0, 20, 0.75, this::setCurrentY))
                .sleep(0.25)
                .then(new Animator(easing, 100, 0, 0.75, this::setCurrentX))
                .sleep(0.25)
                .then(new Animator(easing, 20, 0, 0.75, this::setCurrentY))
                .callback(() -> OctoLib.LOGGER.info("This callback was sent by the {} widget!", this.easing.name()))
                .start();

        super.onClick(mouseX, mouseY);
    }
}
