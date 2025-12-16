package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.client.config.UISprite;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.Nullable;

public class IconButtonWidget<E extends AbstractWidget> extends AbstractChildWidget<E> {
    UISprite.Single icon;
    final Runnable onPress;

    public IconButtonWidget(int x, int y, int width, int height, Runnable onPress, @Nullable UISprite.Single icon) {
        super(x, y, width, height);
        this.onPress = onPress;
        this.icon = icon;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // TODO: render button
        UIElements.BUTTON.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY(), this.width, this.height+1, 0xff666666);

        if (icon == null) {
            return;
        }

        int x = this.width / 2 - icon.getWidth() / 2 + this.getX();
        int y = this.height / 2 - icon.getHeight() / 2 + this.getY() - 1;
        icon.render(guiGraphics, RenderPipelines.GUI_TEXTURED, x, y);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        this.onPress.run();
    }
}
