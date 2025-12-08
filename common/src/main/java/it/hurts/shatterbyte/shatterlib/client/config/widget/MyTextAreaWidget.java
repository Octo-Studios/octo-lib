package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class MyTextAreaWidget extends EditBox implements Child<TextAreaWidget> {
    TextAreaWidget parent;

    public MyTextAreaWidget(Font font, int width, int height, TextAreaWidget parent) {
        super(font, width, height, Component.literal(parent.getValue()));
        this.setParent(parent);
        this.setValue(parent.getValue());
        this.setResponder(parent::setValue);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isBordered()) {
            UIElements.TEXT_AREA.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        guiGraphics.drawString(this.parent.font, this.getValue(), 4, 4, 0xffffffff, true);
    }

    @Override
    public String getValue() {
        return parent.getValue();
    }

    @Override
    public @Nullable TextAreaWidget getParent() {
        return parent;
    }

    @Override
    public boolean isHovered() {
        return parent.isHovered();
    }

    @Override
    public void setParent(@Nullable TextAreaWidget parent) {
        this.parent = parent;
    }
}
