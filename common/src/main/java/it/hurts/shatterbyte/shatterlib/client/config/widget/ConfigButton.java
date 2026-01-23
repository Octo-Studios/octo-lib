package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
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
        RenderUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.width, this.height, 0xff0000ff);
        guiGraphics.drawString(Minecraft.getInstance().font, config.getPath(), this.getX() + 2, this.getY() + 2, 0xffffffff, true);
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
