package it.hurts.shatterbyte.shatterlib.client.config;

import java.util.HashMap;
import java.util.Map;

public final class EntryWidgetRegistry {
    private static final Map<Class<?>, EntryWidgetFactory<?>> FACTORIES = new HashMap<>();

    private EntryWidgetRegistry() {}

    public static <E> void register(Class<E> entryClass, EntryWidgetFactory<E> factory) {
        FACTORIES.put(entryClass, factory);
    }

    @SuppressWarnings("unchecked")
    public static <E> EntryWidgetFactory<E> getFactory(Class<?> cls) {
        return (EntryWidgetFactory<E>) FACTORIES.get(cls);
    }
}
