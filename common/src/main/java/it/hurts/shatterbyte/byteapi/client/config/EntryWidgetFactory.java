package it.hurts.shatterbyte.byteapi.client.config;

import it.hurts.shatterbyte.byteapi.client.config.widget.PathContainerWidget;
import it.hurts.shatterbyte.byteapi.module.config.ShatterConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface EntryWidgetFactory<E> {
    AbstractEntryWidget<?> create(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, E defaultValue, Supplier<E> getter, Consumer<E> setter);
}