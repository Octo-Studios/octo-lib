package it.hurts.shatterbyte.shatterlib;

import it.hurts.shatterbyte.shatterlib.client.animation.TweenSystem;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetFactory;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetRegistry;
import it.hurts.shatterbyte.shatterlib.client.config.widget.*;
import it.hurts.shatterbyte.shatterlib.client.screen.TestGearScreen;
import it.hurts.shatterbyte.shatterlib.module.chromatic_aberration.ChromaticAberrationManager;
import it.hurts.shatterbyte.shatterlib.module.chromatic_aberration.misc.S2CChromaticAberrationPacket;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.MyClientConfig;
import it.hurts.shatterbyte.shatterlib.module.config.network.SyncServerConfigPacket;
import it.hurts.shatterbyte.shatterlib.module.config.network.TestScreenPacket;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Range;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import it.hurts.shatterbyte.shatterlib.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.shatterlib.module.particle.trail.EntityTrailRegistry;
import it.hurts.shatterbyte.shatterlib.module.particle.trail.TestArrowTrail;
import it.hurts.shatterbyte.shatterlib.module.post_effect.init.ShatterLibPostEffects;
import it.hurts.shatterbyte.shatterlib.module.post_effect.instances.ChromaticAberrationPostEffect;
import it.hurts.shatterbyte.shatterlib.module.camera_shake.CameraShakeManager;
import it.hurts.shatterbyte.shatterlib.module.camera_shake.misc.S2CCameraShakePacket;
import it.hurts.shatterbyte.shatterlib.platform.ShatterLibServices;
import it.hurts.shatterbyte.shatterlib.util.DeltaTimeTracker;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ShatterLibClient {
    public static final ShatterConfig CONFIG = new MyClientConfig();

    public static void init() {
        ShatterLibNetwork.registerS2CReceiver(TestScreenPacket.TYPE, TestScreenPacket.STREAM_CODEC, value -> {
            Minecraft.getInstance().setScreen(new TestGearScreen());
        });

        ShatterLibNetwork.registerS2CReceiver(SyncServerConfigPacket.TYPE, SyncServerConfigPacket.STREAM_CODEC, value -> {
            ShatterConfig config = ConfigManager.getConfig(value.path);
            if (config == null) {
                return;
            }

            config.loadFromJson(value.json);
            config.updateSchemaCache();
        });

        ShatterLibNetwork.registerS2CReceiver(S2CChromaticAberrationPacket.TYPE, S2CChromaticAberrationPacket.STREAM_CODEC, value -> {
            var player = Minecraft.getInstance().player;

            if (player == null)
                return;

            ChromaticAberrationManager.add(player.level(), value.getChromaticAberration());
        });

        ShatterLibNetwork.registerS2CReceiver(S2CCameraShakePacket.TYPE, S2CCameraShakePacket.STREAM_CODEC, value -> {
            var player = Minecraft.getInstance().player;

            if (player == null)
                return;

            CameraShakeManager.add(player.level(), value.getShake());
        });

        ShatterLibPostEffects.register(ChromaticAberrationPostEffect::new);
        ShatterLibPostEffects.init();

        TweenSystem.init();
        //EntityTrailRegistry.registerProvider(EntityType.ARROW, TestArrowTrail::new);
        ShatterLibClient.registerEntryWidgets();

        if (ShatterLibServices.platform().isDevelopmentEnvironment()) {
            ConfigManager.register(ShatterLib.MOD_ID, CONFIG);
        }
    }

    private static void registerEntryWidgets() {
        EntryWidgetRegistry.registerConstructor(Number.class, clazz -> 0d);
        EntryWidgetRegistry.registerConstructor(byte.class, clazz -> (byte) 0);
        EntryWidgetRegistry.registerConstructor(Byte.class, clazz -> (byte) 0);
        EntryWidgetRegistry.registerConstructor(short.class, clazz -> (short) 0);
        EntryWidgetRegistry.registerConstructor(Short.class, clazz -> (short) 0);
        EntryWidgetRegistry.registerConstructor(int.class, clazz -> 0);
        EntryWidgetRegistry.registerConstructor(Integer.class, clazz -> 0);
        EntryWidgetRegistry.registerConstructor(long.class, clazz -> 0L);
        EntryWidgetRegistry.registerConstructor(Long.class, clazz -> 0L);
        EntryWidgetRegistry.registerConstructor(float.class, clazz -> 0f);
        EntryWidgetRegistry.registerConstructor(Float.class, clazz -> 0f);
        EntryWidgetRegistry.registerConstructor(double.class, clazz -> 0d);
        EntryWidgetRegistry.registerConstructor(Double.class, clazz -> 0d);
        EntryWidgetRegistry.registerConstructor(List.class, clazz -> new ArrayList<>());
        EntryWidgetRegistry.registerConstructor(Map.class, clazz -> new HashMap<>());
        EntryWidgetRegistry.registerConstructor(Boolean.class, clazz -> false);
        EntryWidgetRegistry.registerConstructor(Enum.class, clazz -> clazz.getEnumConstants()[0]);
        EntryWidgetRegistry.registerConstructor(String.class, clazz -> "");
        EntryWidgetRegistry.registerConstructor(ShatterColor.class, clazz -> ShatterColor.WHITE);
        EntryWidgetRegistry.registerConstructor(Identifier.class, clazz -> Identifier.parse("minecraft:empty"));
        EntryWidgetRegistry.registerConstructor(Item.class, clazz -> Items.AIR);

        EntryWidgetRegistry.register(Object.class, GenericObjectWidget::new);
        EntryWidgetRegistry.register(boolean.class, (EntryWidgetFactory<Boolean>) ToggleWidget::new);
        EntryWidgetRegistry.register(Boolean.class, (EntryWidgetFactory<Boolean>) ToggleWidget::new);
        EntryWidgetRegistry.register(String.class, (EntryWidgetFactory<String>) TextAreaWidget::new);
        EntryWidgetRegistry.register(Identifier.class, (EntryWidgetFactory<Identifier>) IdentifierWidget::new);
        EntryWidgetRegistry.register(Item.class, (EntryWidgetFactory<Item>) ItemWidget::new);
        EntryWidgetRegistry.register(ShatterColor.class, (EntryWidgetFactory<ShatterColor>) ShatterColorWidget::new);

        EntryWidgetFactory<Number> numberFactory = (config, type, annotations, parent, defaultValue, getter, setter) -> {
            boolean hasRange = false;
            Range range = null;
            for (Annotation annotation : annotations) {
                if (annotation instanceof Range rangeAnnotation) {
                    hasRange = true;
                    range = rangeAnnotation;
                }
            }

            if (!hasRange) {
                final String[] numberText = {String.valueOf(getter.get())};
                TextAreaWidget text = new TextAreaWidget(
                        config,
                        type,
                        annotations,
                        parent,
                        String.valueOf(defaultValue),
                        () -> numberText[0],
                        s -> {
                            numberText[0] = s;
                            try {
                                setter.accept(TextAreaWidget.parseNumber(type, s));
                            } catch (NumberFormatException ignored) {}
                        }
                );

                text.setOnBlur(() -> {
                    String currentText = numberText[0];
                    try {
                        setter.accept(TextAreaWidget.parseNumber(type, currentText));
                    } catch (NumberFormatException ignored) {
                        String rollback = String.valueOf(getter.get());
                        numberText[0] = rollback;
                        text.setValue(rollback);
                    }
                });

                return text;
            }

            return new SliderWidget<>(config, range, parent, defaultValue, getter, setter);
        };

        EntryWidgetRegistry.register(Number.class, numberFactory);
        EntryWidgetRegistry.register(float.class, numberFactory);
        EntryWidgetRegistry.register(double.class, numberFactory);
        EntryWidgetRegistry.register(int.class, numberFactory);
        EntryWidgetRegistry.register(short.class, numberFactory);
        EntryWidgetRegistry.register(byte.class, numberFactory);

        EntryWidgetRegistry.register(Enum.class, (EntryWidgetFactory<Enum>) EnumDropdownWidget::new);
        EntryWidgetRegistry.register(List.class, (EntryWidgetFactory<List>) ListWidget::new);
        EntryWidgetRegistry.register(Map.class, (EntryWidgetFactory<Map>) MapWidget::new);
    }

    public static void onClientLevelPre(ClientLevel level) {
        ShatterRenderManager.clientTick(level);
    }

    public static void onClientDisconnect() {
        ShatterRenderManager.worldExit();
    }

    public static double getDeltaTime() {
        return DeltaTimeTracker.getDeltaSeconds();
    }
}
