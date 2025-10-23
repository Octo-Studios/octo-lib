package it.hurts.shatterbyte.shatterlib.client.config;

import it.hurts.shatterbyte.shatterlib.module.config.type.AbstractEntry;

import java.util.HashMap;
import java.util.Map;

public final class EntryWidgetRegistry {
    private static final Map<Class<? extends AbstractEntry<?, ?>>, EntryWidgetFactory<?>> FACTORIES = new HashMap<>();

    private EntryWidgetRegistry() {}

    public static <E extends AbstractEntry<?, E>> void register(Class<E> entryClass, EntryWidgetFactory<E> factory) {
        FACTORIES.put(entryClass, factory);
    }

    @SuppressWarnings("unchecked")
    public static <E extends AbstractEntry<?, E>> EntryWidgetFactory<E> getFactory(Class<? extends AbstractEntry<?, ?>> cls) {
        EntryWidgetFactory<E> f = (EntryWidgetFactory<E>) FACTORIES.get(cls);
        if (f != null) {
            return f;
        }

        // fallback: walk superclasses
        Class<?> cur = cls.getSuperclass();
        while (cur != null && AbstractEntry.class.isAssignableFrom(cur)) {
            f = (EntryWidgetFactory<E>) FACTORIES.get(cur);
            if (f != null) return f;
            cur = cur.getSuperclass();
        }

        return null;
    }
}
