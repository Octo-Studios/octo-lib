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
    public static <E> EntryWidgetFactory<E> getFactory(Class<?> clazz) {
        Class<?> current = clazz;
        while (current != null) {
            EntryWidgetFactory<?> factory = FACTORIES.get(current);
            if (factory != null) {
                return (EntryWidgetFactory<E>) factory;
            }

            for (Class<?> interfaceClass : current.getInterfaces()) {
                factory = FACTORIES.get(interfaceClass);
                if (factory != null) {
                    return (EntryWidgetFactory<E>) factory;
                }
            }

            current = current.getSuperclass();
        }

        return null;
    }
}
