package it.hurts.shatterbyte.shatterlib.client.config;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import lombok.SneakyThrows;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigScreen extends Screen {
    Screen prevScreen;

    public ConfigScreen(Screen prevScreen) {
        super(Component.empty());
        this.prevScreen = prevScreen;
    }

    @Override
    protected void init() {
        super.init();
        try {
            Class<?> clazz = ShatterLib.CONFIG.getClass();

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(clazz, lookup);

            int y = 20;
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                int mods = field.getModifiers();
                if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) {
                    continue;
                }

                AbstractEntryWidget<?> widget = ConfigScreen.createWidgetFromField(privateLookup, field, ShatterLib.CONFIG, 20, y);
                if (widget == null) {
                    continue;
                }

                this.addRenderableWidget(widget);
                y+=20;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //this.addRenderableWidget(new CheckboxWidget(ShatterLib.CONFIG::isTestBool, ShatterLib.CONFIG::setTestBool, 20, 20, 16, 16, Component.empty()));
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static <T> AbstractEntryWidget<T> createWidgetFromField(MethodHandles.Lookup lookup, Field field, Object rootObject, int x, int y) {
        EntryWidgetFactory<T> factory = EntryWidgetRegistry.getFactory(field.getType());

        if (factory == null) {
            return null;
        }

        //VarHandle vh = lookup.findVarHandle(rootObject.getClass(), field.getName(), field.getType());
        MethodHandle getterHandle = lookup.unreflectGetter(field).bindTo(rootObject);
        MethodHandle setterHandle = lookup.unreflectSetter(field).bindTo(rootObject);

        Supplier<Object> getter = getterHandle::invoke;
        Consumer<T> setter = setterHandle::invoke;

        AbstractEntryWidget<T> widget = factory.create(
                (Supplier<T>) getter, setter,
                x, y, 64, 16, Component.literal(field.getName())
        );

        return widget;
    }

    @Override
    public void onClose() {
        ShatterLib.CONFIG.save();
        this.minecraft.setScreen(prevScreen);
    }
}
