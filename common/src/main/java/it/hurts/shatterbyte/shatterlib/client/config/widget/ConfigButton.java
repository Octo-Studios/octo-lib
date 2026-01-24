package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ConfigButton extends AbstractWidget implements Child<ScrollableWidget>, Scrollable {
    ShatterConfig config;
    Runnable onClick;
    private double scrollOffset;

    public ConfigButton(ShatterConfig config, int x, int y, int width, int height, Runnable onClick) {
        super(x, y, width, height, Component.empty());
        this.config = config;
        this.onClick = onClick;
    }

    @Override
    public int getY() {
        return super.getY() + (int) scrollOffset;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //RenderUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.width, this.height, 0xff0000ff);
        int iconOffset = 0;
        if (this.isActive()) {
            if (this.isHovered()) {
                UIElements.BUTTON_HOVERED.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY(), this.width, this.height + 1, 0xff555555);
                iconOffset = 1;
            } else {
                UIElements.BUTTON.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY(), this.width, this.height + 1, 0xff666666);
            }

        } else {
            UIElements.BUTTON_PRESSED.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY(), this.width, this.height + 1, 0xff666666);
            iconOffset = 2;
        }


        Font font = Minecraft.getInstance().font;
        String configPath = config.getPath();
        guiGraphics.drawString(font, configPath, this.getX() + 4, this.getY() + 4 + iconOffset, 0xffffffff, true);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.getX() + 4, this.getY() + 14 + iconOffset);
        guiGraphics.pose().scale(0.5f);
        guiGraphics.drawString(Minecraft.getInstance().font, config.getComment().split("\n")[0], 0, 0, 0xff888888, true);
        guiGraphics.pose().popMatrix();
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        onClick.run();
        super.onClick(event, isDoubleClick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public @Nullable ScrollableWidget getParent() {
        return null;
    }

    @Override
    public void setParent(@Nullable ScrollableWidget parent) {

    }

    @Override
    public double getScrollOffset() {
        return scrollOffset;
    }

    @Override
    public void setScrollOffset(double offset) {
        this.scrollOffset = offset;
    }
}
