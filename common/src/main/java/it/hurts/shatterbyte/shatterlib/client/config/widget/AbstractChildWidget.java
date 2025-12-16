package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractChildWidget<E extends AbstractWidget> extends AbstractWidget implements Child<E> {
    E parent;

    public AbstractChildWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    @Override
    public @Nullable E getParent() {
        return parent;
    }

    @Override
    public void setParent(E parent) {
        this.parent = parent;
    }
}
