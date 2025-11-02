package it.hurts.shatterbyte.shatterlib.client.config;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface EntryWidgetFactory<E> {
    /**
     * Create a widget for the given entry.
     * @param x left coordinate
     * @param y top coordinate
     * @param width widget width
     * @param height widget height
     */
    AbstractEntryWidget<E> create(Supplier<E> getter, Consumer<E> setter, int x, int y, int width, int height, Component name);
}