package it.hurts.shatterbyte.shatterlib.client.screen.widget;

import it.hurts.shatterbyte.shatterlib.mixin.AbstractWidgetAccessor;
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
        return parent != null ? new Vector2i(parent.getX(), parent.getY()) : new Vector2i(0, 0);
    }

    default Vector2i getPosition() {
        Vector2i parentPos = getParentPosition();
        return new Vector2i(getLocalX() + parentPos.x, getLocalY() + parentPos.y);
    }

    @Override
    default @NotNull ScreenRectangle getRectangle() {
        return LayoutElement.super.getRectangle();
    }

    default void detachWidget() {
        Vector2i globalPos = getPosition();
        setParent(null);
        setPosition(globalPos.x, globalPos.y);
    }

    default void attachWidget(@Nullable T parent) {
        Vector2i globalPos = getPosition();
        setParent(parent);
        Vector2i parentPos = getParentPosition();
        setPosition(globalPos.x - parentPos.x, globalPos.y - parentPos.y);
    }
}