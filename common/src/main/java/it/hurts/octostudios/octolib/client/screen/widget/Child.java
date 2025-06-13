package it.hurts.octostudios.octolib.client.screen.widget;

import it.hurts.octostudios.octolib.util.RenderUtils;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2i;

public interface Child<T extends LayoutElement> extends LayoutElement, GuiEventListener {
    @Nullable T getParent();
    void setParent(@Nullable T parent);

    int getLocalX();
    int getLocalY();

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

    default int getX() {
        return getPosition().x;
    }

    default int getY() {
        return getPosition().y;
    }

    @Override
    default ScreenRectangle getRectangle() {
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