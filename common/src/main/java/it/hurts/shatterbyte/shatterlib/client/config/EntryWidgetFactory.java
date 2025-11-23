package it.hurts.shatterbyte.shatterlib.client.config;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface EntryWidgetFactory<E> {
    AbstractEntryWidget<E> create(E defaultValue, Supplier<E> getter, Consumer<E> setter);
}