package it.hurts.octostudios.octolib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TestWidget extends AbstractWidget {
    private int cX;
    private int cY;
    private int oX;
    private int oY;
    public Easing easing;

    public TestWidget(int x, int y, Easing easing) {
        super(x, y, 8, 8, Component.literal(easing.name()));
        this.easing = easing;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float partialTick) {
        float actualPartialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        float x = Mth.lerp(actualPartialTick, oX, cX) + this.getX();
        float y = Mth.lerp(actualPartialTick, oY, cY) + this.getY();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
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

    public void setCurrentX(int x) {
        this.oX = this.cX;
        this.cX = x;
    }

    public void setCurrentY(int y) {
        this.oY = this.cY;
        this.cY = y;
    }
}
