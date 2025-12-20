package it.hurts.shatterbyte.shatterlib.client.config.widget;

import net.minecraft.client.gui.layouts.LayoutElement;

public interface PathContainerWidget extends LayoutElement {
    String getPath();
    void moveToTheTop();
}
