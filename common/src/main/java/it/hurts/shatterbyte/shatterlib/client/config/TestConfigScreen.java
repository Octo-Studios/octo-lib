package it.hurts.shatterbyte.shatterlib.client.config;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.config.widget.CheckboxWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.util.Cast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestConfigScreen extends Screen {
    Screen prevScreen;

    public TestConfigScreen(Screen prevScreen) {
        super(Component.empty());
        this.prevScreen = prevScreen;
    }

    @Override
    protected void init() {
        super.init();
        try {
            Class<?> clazz = ShatterLib.CONFIG.getClass();
            int y = 20;
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                int mods = field.getModifiers();
                if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) {
                    continue;
                }

                Object value = field.get(ShatterLib.CONFIG);

                EntryWidgetFactory<?> factory = EntryWidgetRegistry.getFactory(value.getClass());

                if (factory == null) {
                    continue;
                }

                AbstractEntryWidget<?> widget = factory.create(
                        () -> {
                            try {
                                return Cast.cast(field.get(ShatterLib.CONFIG));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        set -> {
                            try {
                                field.set(ShatterLib.CONFIG, set);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }, 20, y, 64, 16, Component.literal(field.getName())
                );

                this.addRenderableWidget(widget);
                y+=20;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClose() {
        ShatterLib.CONFIG.save();
        this.minecraft.setScreen(prevScreen);
    }
}
