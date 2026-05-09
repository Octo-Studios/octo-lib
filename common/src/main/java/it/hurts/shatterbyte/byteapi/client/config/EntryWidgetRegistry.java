package it.hurts.shatterbyte.byteapi.client.config;

import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class EntryWidgetRegistry {
    private static final Map<Class<?>, EntryWidgetFactory<?>> FACTORIES = new HashMap<>();
    private static final Map<Class<?>, Function<Class<?>, ?>> DEFAULT_CONSTRUCTORS = new HashMap<>();

    private EntryWidgetRegistry() {}

    public static <E> void register(Class<E> entryClass, EntryWidgetFactory<?> factory) {
        FACTORIES.put(entryClass, factory);
    }

    public static <E> void registerConstructor(Class<E> entryClass, Function<Class<?>, ?> constructor) {
        DEFAULT_CONSTRUCTORS.put(entryClass, constructor);
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

    @SuppressWarnings("unchecked")
    public static <E> E getDefaultValue(Class<E> clazz) {
        Class<?> current = clazz;
        while (current != null) {
            Function<Class<?>, ?> factory = DEFAULT_CONSTRUCTORS.get(current);
            if (factory != null) {
                return (E) factory.apply(clazz);
            }

            for (Class<?> interfaceClass : current.getInterfaces()) {
                factory = DEFAULT_CONSTRUCTORS.get(interfaceClass);
                if (factory != null) {
                    return (E) factory.apply(clazz);
                }
            }

            current = current.getSuperclass();
        }

        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            Minecraft.getInstance().delayCrash(CrashReport.forThrowable(e, "No default constructor found for class "+clazz.getName()));
        }

        return null;
    }
}
