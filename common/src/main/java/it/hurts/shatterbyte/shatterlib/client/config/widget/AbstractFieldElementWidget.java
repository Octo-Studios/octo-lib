package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFieldElementWidget extends AbstractWidget implements Child<FieldWidget> {
    FieldWidget parent;

    public AbstractFieldElementWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    @Override
    public @Nullable FieldWidget getParent() {
        return parent;
    }

    @Override
    public void setParent(FieldWidget parent) {
        this.parent = parent;
    }
}
