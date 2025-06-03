package it.hurts.octostudios.octolib.util;

import it.hurts.octostudios.octolib.AnimatorSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TestWidget extends AbstractWidget {
    private double cX;
    private double cY;
    public Easing easing;

    public TestWidget(int x, int y, Easing easing) {
        super(x, y, 8, 8, Component.literal(easing.name()));
        this.easing = easing;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float partialTick) {
        float actualPartialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(cX+this.getX(), cY+this.getY(), 0);
        guiGraphics.renderOutline(0, 0, this.getWidth(), this.getHeight(), 0xffffffff);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0);
        guiGraphics.renderOutline(-2, -12, 112, this.getHeight()+14, 0x50ffffff);
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
    public void onClick(double mouseX, double mouseY) {
        AnimatorSystem.addAnimator("testWidget"+easing.name(), new Animator(easing, 0, 100, 0.75,
                this::setCurrentX,
                () -> {
                    AnimatorSystem.addAnimator("testWidget"+easing.name(), new Animator(easing, 100, 0, 0.75,
                            this::setCurrentX,
                            () -> {

                            }
                    ));
                }
                ));
        super.onClick(mouseX, mouseY);
    }
}
