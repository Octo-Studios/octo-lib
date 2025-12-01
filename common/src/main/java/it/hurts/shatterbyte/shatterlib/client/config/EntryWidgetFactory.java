package it.hurts.shatterbyte.shatterlib.client.config;

import it.hurts.shatterbyte.shatterlib.client.config.widget.FieldWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface EntryWidgetFactory<E> {
    AbstractEntryWidget<E> create(ShatterConfig config, FieldWidget parent, E defaultValue, Supplier<E> getter, Consumer<E> setter);
}