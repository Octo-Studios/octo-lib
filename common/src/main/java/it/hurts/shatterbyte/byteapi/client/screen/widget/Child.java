package it.hurts.shatterbyte.byteapi.client.screen.widget;

import it.hurts.shatterbyte.byteapi.mixin.AbstractWidgetAccessor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

public interface Child<T extends LayoutElement> extends LayoutElement, GuiEventListener {
    @Nullable T getParent();
    void setParent(@Nullable T parent);

    default int getGlobalX() {
        T parent = getParent();
        if (parent == null) {
            return getLocalX();
        }

        return getLocalX() + parent.getX();
    }

    default int getGlobalY() {
        T parent = getParent();
        if (parent == null) {
            return getLocalY();
        }

        return getLocalY() + parent.getY();
    }

    default int getLocalX() {
        if (this instanceof AbstractWidget widget) {
            return ((AbstractWidgetAccessor) widget).getLocalX();
        }

        return 0;
    }

    default int getLocalY() {
        if (this instanceof AbstractWidget widget) {
            return ((AbstractWidgetAccessor) widget).getLocalY();
        }

        return 0;
    }

    default Vector2i getLocalPosition() {
        return new Vector2i(getLocalX(), getLocalY());
    }

    default Vector2i getParentPosition() {
        T parent = getParent();
        if (parent == null) {
            return new Vector2i(0, 0);
        }

        if (parent instanceof Child<?> childParent) {
            return new Vector2i(childParent.getGlobalX(), childParent.getGlobalY());
        }

        return new Vector2i(parent.getX(), parent.getY());
    }

    default Vector2i getPosition() {
        return new Vector2i(getGlobalX(), getGlobalY());
    }

    @Override
    default @NotNull ScreenRectangle getRectangle() {
        return LayoutElement.super.getRectangle();
    }

    default void detachWidget() {
        int globalX = getGlobalX();
        int globalY = getGlobalY();
        setParent(null);
        setPosition(globalX, globalY);
    }

    default void attachWidget(@Nullable T parent) {
        int globalX = getGlobalX();
        int globalY = getGlobalY();
        setParent(parent);

        int parentX = 0;
        int parentY = 0;
        if (parent != null) {
            if (parent instanceof Child<?> childParent) {
                parentX = childParent.getGlobalX();
                parentY = childParent.getGlobalY();
            } else {
                parentX = parent.getX();
                parentY = parent.getY();
            }
        }

        setPosition(globalX - parentX, globalY - parentY);
    }
}
