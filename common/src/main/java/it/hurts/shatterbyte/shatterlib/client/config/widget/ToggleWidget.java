package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ToggleWidget extends AbstractEntryWidget<Boolean> {
    public ToggleWidget(ShatterConfig config, PathContainerWidget parent, Boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 20, 10);
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.getValue()) {
            UIElements.TOGGLE_ENABLED.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY()+1);
            UIElements.TOGGLE_THINGY.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX()+10, this.getY());
        } else {
            UIElements.TOGGLE_DISABLED.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY()+1);
            UIElements.TOGGLE_THINGY.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY());
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        super.onClick(event, isDoubleClick);
        this.setValue(!this.getValue());
    }
}
