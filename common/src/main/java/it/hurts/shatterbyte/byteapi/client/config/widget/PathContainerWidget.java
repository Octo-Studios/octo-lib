package it.hurts.shatterbyte.byteapi.client.config.widget;

import net.minecraft.client.gui.layouts.LayoutElement;

public interface PathContainerWidget extends LayoutElement {
    String getPath();
    void moveToTheTop();
    void requestRelayout();
}
