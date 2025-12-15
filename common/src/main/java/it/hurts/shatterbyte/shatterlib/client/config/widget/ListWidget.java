package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ListWidget<E> extends AbstractEntryWidget<ArrayList<E>> implements DynamicallySized {
    public ListWidget(ShatterConfig config, FieldWidget parent, ArrayList<E> defaultValue, Supplier<ArrayList<E>> getter, Consumer<ArrayList<E>> setter, int x, int y, int width, int height) {
        super(config, parent, defaultValue, getter, setter, x, y, width, height);
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    public void repositionElements() {

    }
}
