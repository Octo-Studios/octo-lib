package it.hurts.octostudios.octolib.client;

import it.hurts.octostudios.octolib.client.animation.EaseType;
import it.hurts.octostudios.octolib.client.animation.TransitionType;
import it.hurts.octostudios.octolib.client.animation.Tween;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TestWidget extends AbstractWidget {
    public double cX;
    public double cY;
    public TransitionType transitionType;
    public EaseType easeType;
    public Tween tween;

    public TestWidget(int x, int y, TransitionType transitionType, EaseType easeType) {
        super(x, y, 8, 8, Component.literal(easeType.name()+"_"+transitionType.name()));
        this.transitionType = transitionType;
        this.easeType = easeType;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float partialTick) {
        float actualPartialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(cX + this.getX() + test, cY + this.getY(), 0);
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
        return hovered;
    }

    private double test;

    @Override
    public void onClick(double mouseX, double mouseY) {
//        if (animator != null) {
//            this.setCurrentX(0);
//            this.setCurrentY(0);
//            animator.stop();
//        }

//        this.animator = new OldAnimator(easing, 0, 100, 0.75, this::setCurrentX, () -> OctoLib.LOGGER.info("test callback"))
//                .sleep(0.25)
//                .then(new OldAnimator(easing, 0, 20, 0.75, this::setCurrentY))
//                .sleep(0.25)
//                .then(new OldAnimator(easing, 100, 0, 0.75, this::setCurrentX))
//                .callback(() -> OctoLib.LOGGER.info("This callback was sent by the {} widget!", this.easing.name()))
//                .start();
//
//        ShakeSystem.startShake((Shakeable) this,
//                new ShakeData(0.25f, 1f, 5)
//                        .withTimeEasing(this.easing)
//        );
//
        if (tween != null) {
            tween.kill();
        }

        tween = Tween.create().setTransitionType(transitionType).setEase(easeType).setLoops(-1);
        //tween.tweenProperty(this, "cX", 100, 1);
        tween.tweenProperty(this, "cY", 20, 0.5);
        tween.tweenProperty(this, "cY", 0, 0.5);
//        tween.tweenProperty(this, "cX", 0, 1);
//        tween.tweenProperty(this, "cY", 0, 0.5);


        super.onClick(mouseX, mouseY);
    }
}
