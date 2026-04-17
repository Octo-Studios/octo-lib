package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
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
    private static final int CONTENT_PADDING_X = 4;
    private static final int TITLE_TOP = 4;
    private static final int DESCRIPTION_TOP = 14;
    private static final float DESCRIPTION_SCALE = 0.5f;
    private static final double SCROLL_SPEED = 30.0;
    private static final double SCROLL_PAUSE_SECONDS = 0.75;

    ShatterConfig config;
    Runnable onClick;
    private double scrollOffset;
    private @Nullable ScrollableWidget parent;

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
        String title = config.getPath();
        if (title == null) {
            title = "";
        }

        String description = firstLine(config.getComment());
        int contentX = this.getX() + CONTENT_PADDING_X;
        int contentWidth = Math.max(0, this.width - CONTENT_PADDING_X * 2);

        drawScrollingLine(
                guiGraphics, font, title, contentX, this.getY() + TITLE_TOP + iconOffset, contentWidth, font.lineHeight, 1.0f, 0xffffffff
        );
        drawScrollingLine(
                guiGraphics, font, description, contentX, this.getY() + DESCRIPTION_TOP + iconOffset, contentWidth,
                Math.max(1, Math.round(font.lineHeight * DESCRIPTION_SCALE)), DESCRIPTION_SCALE, 0xff888888
        );
    }

    private void drawScrollingLine(GuiGraphics guiGraphics, Font font, String text, int x, int y, int width, int height, float scale, int color) {
        if (width <= 0 || height <= 0 || text.isEmpty()) {
            return;
        }

        double textWidth = font.width(text) * scale;
        int textOffset = 0;

        if (textWidth > width) {
            textOffset = (int) Math.round(computeScrollOffset(textWidth - width));
        }

        guiGraphics.enableScissor(x, y, x + width, y + height);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x - textOffset, y);
        guiGraphics.pose().scale(scale);
        guiGraphics.drawString(font, text, 0, 0, color, true);
        guiGraphics.pose().popMatrix();
        guiGraphics.disableScissor();
    }

    private double computeScrollOffset(double maxOffset) {
        if (maxOffset <= 0) {
            return 0;
        }

        double travelSeconds = maxOffset / SCROLL_SPEED;
        double cycleSeconds = SCROLL_PAUSE_SECONDS * 2 + travelSeconds * 2;
        double time = System.currentTimeMillis() / 1000.0;
        double phase = time % cycleSeconds;

        if (phase < SCROLL_PAUSE_SECONDS) {
            return 0;
        }

        phase -= SCROLL_PAUSE_SECONDS;
        if (phase < travelSeconds) {
            return phase / travelSeconds * maxOffset;
        }

        phase -= travelSeconds;
        if (phase < SCROLL_PAUSE_SECONDS) {
            return maxOffset;
        }

        phase -= SCROLL_PAUSE_SECONDS;
        return maxOffset - (phase / travelSeconds * maxOffset);
    }

    private String firstLine(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        int lineBreak = text.indexOf('\n');
        return lineBreak == -1 ? text : text.substring(0, lineBreak);
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
        return parent;
    }

    @Override
    public void setParent(@Nullable ScrollableWidget parent) {
        this.parent = parent;
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
