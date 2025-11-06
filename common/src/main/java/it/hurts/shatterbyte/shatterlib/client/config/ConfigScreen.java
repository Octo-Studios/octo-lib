package it.hurts.shatterbyte.shatterlib.client.config;

import de.marhali.json5.Json5Element;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;
import lombok.SneakyThrows;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigScreen extends Screen {
    ShatterConfig config;
    Screen prevScreen;

    public ConfigScreen(ShatterConfig config, Screen prevScreen) {
        super(Component.literal(config.getPath()));
        this.config = config;
        this.prevScreen = prevScreen;
    }

    @Override
    protected void init() {
        super.init();
        getWidgetsForObject(20, config, config, "").forEach(this::addRenderableWidget);
    }

    private static Collection<AbstractEntryWidget<?>> getWidgetsForObject(int startingY, ShatterConfig config, Object rootObject, String path) {
        try {
            List<AbstractEntryWidget<?>> widgetsToAdd = new ArrayList<>();
            Class<?> clazz = rootObject.getClass();

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(clazz, lookup);

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(Exclude.class)) {
                    continue;
                }

                int mods = field.getModifiers();
                if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) {
                    continue;
                }

                AbstractEntryWidget<?> widget = ConfigScreen.createWidgetFromField(path, config, privateLookup, field, rootObject, 20, startingY);
                if (widget == null) {
                    String newPath = path + "." + field.getName();
                    if (newPath.startsWith(".")) {
                        newPath = newPath.substring(1);
                    }

                    widgetsToAdd.addAll(getWidgetsForObject(startingY, config, field.get(rootObject), newPath));
                    continue;
                }

                widgetsToAdd.add(widget);
                startingY += 20;
            }

            return widgetsToAdd;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static <T> AbstractEntryWidget<T> createWidgetFromField(String path, ShatterConfig config, MethodHandles.Lookup lookup, Field field, Object rootObject, int x, int y) {
        EntryWidgetFactory<T> factory = EntryWidgetRegistry.getFactory(field.getType());
        if (factory == null) {
            return null;
        }

        MethodHandle getterHandle = lookup.unreflectGetter(field).bindTo(rootObject);
        MethodHandle setterHandle = lookup.unreflectSetter(field).bindTo(rootObject);

        Supplier<Object> getter = getterHandle::invoke;
        Consumer<T> setter = setterHandle::invoke;

        String fieldName = field.getName();
        String newPath = path + "." + fieldName;
        if (newPath.startsWith(".")) {
            newPath = newPath.substring(1);
        }

        Optional<T> defaultValue = config.getDefaultValue(newPath, field.getGenericType());

        if (defaultValue.isEmpty()) {
            throw new RuntimeException("Default value for " + newPath + " not found.");
        }

        long count = newPath.chars()
                .filter(c -> c == '.')
                .count();

        AbstractEntryWidget<T> widget = factory.create(defaultValue.get(),
                (Supplier<T>) getter,
                setter,
                (int) (x + 20 * count), y, 64, 16,
                config.getPath(),
                fieldName
        );

        return widget;
    }

    @Override
    public void onClose() {
        config.save();
        this.minecraft.setScreen(prevScreen);
    }
}
