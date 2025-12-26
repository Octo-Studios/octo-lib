package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import it.hurts.shatterbyte.shatterlib.client.animation.TweenSystem;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetFactory;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetRegistry;
import it.hurts.shatterbyte.shatterlib.client.config.widget.*;
import it.hurts.shatterbyte.shatterlib.client.screen.TestGearScreen;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibClientCommand;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.MyClientConfig;
import it.hurts.shatterbyte.shatterlib.module.config.network.SyncServerConfigPacket;
import it.hurts.shatterbyte.shatterlib.module.config.network.TestScreenPacket;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Range;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import it.hurts.shatterbyte.shatterlib.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.shatterlib.util.DeltaTimeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ShatterLibClient {
    public static final ShatterConfig CONFIG = new MyClientConfig();

    public static void init() {
        registerEvents();

        ShatterLibNetwork.registerS2CReceiver(TestScreenPacket.TYPE, TestScreenPacket.STREAM_CODEC, (value, context) -> {
            Minecraft.getInstance().setScreen(new TestGearScreen());
        });

        ShatterLibNetwork.registerS2CReceiver(SyncServerConfigPacket.TYPE, SyncServerConfigPacket.STREAM_CODEC, (value, context) -> {
            ShatterConfig config = ConfigManager.getConfig(value.path);
            if (config == null) {
                return;
            }

            config.loadFromJson(value.json);
            config.updateSchemaCache();

            context.getPlayer().displayClientMessage(Component.literal("Recieved a sync packet! Path: "+value.path+". Contents: "+value.json), false);
        });

        ClientCommandRegistrationEvent.EVENT.register(ShatterLibClientCommand::register);

        TweenSystem.init();
        //EntityTrailRegistry.registerProvider(EntityType.ARROW, TestArrowTrail::new);

        EntryWidgetRegistry.registerConstructor(Number.class, clazz -> 0);
        EntryWidgetRegistry.registerConstructor(List.class, clazz -> new ArrayList<>());
        EntryWidgetRegistry.registerConstructor(Map.class, clazz -> new HashMap<>());
        EntryWidgetRegistry.registerConstructor(Boolean.class, clazz -> false);
        EntryWidgetRegistry.registerConstructor(Enum.class, clazz -> clazz.getEnumConstants()[0]);
        EntryWidgetRegistry.registerConstructor(String.class, clazz -> "");

        EntryWidgetRegistry.register(Object.class, GenericObjectWidget::new);
        EntryWidgetRegistry.register(boolean.class, ToggleWidget::new);
        EntryWidgetRegistry.register(Boolean.class, ToggleWidget::new);
        EntryWidgetRegistry.register(String.class, TextAreaWidget::new);
        EntryWidgetRegistry.register(Number.class, (config, annotations, parent, defaultValue, getter, setter) -> {
            boolean hasRange = false;
            Range range = null;
            for (Annotation annotation : annotations) {
                if (annotation instanceof Range rangeAnnotation) {
                    hasRange = true;
                    range = rangeAnnotation;
                }
            }

            if (!hasRange) {
                return null;
            }

            var widget = new SliderWidget<>(config, parent, defaultValue, getter, setter);
            widget.setRange(range.min(), range.max(), range.step());
            return widget;
        });
        EntryWidgetRegistry.register(Enum.class, EnumDropdownWidget::new);
        //EntryWidgetRegistry.register(ArrayList.class, ListWidget::new);

        ConfigManager.register(ShatterLib.MOD_ID, CONFIG);
    }
    
    private static void registerEvents() {
        ClientTickEvent.CLIENT_LEVEL_PRE.register(ShatterRenderManager::clientTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(ShatterRenderManager::worldExit);
    }

    public static double getDeltaTime() {
        return DeltaTimeTracker.getDeltaSeconds();
    }
}
