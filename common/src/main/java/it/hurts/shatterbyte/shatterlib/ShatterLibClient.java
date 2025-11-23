package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import it.hurts.shatterbyte.shatterlib.client.animation.TweenSystem;
import it.hurts.shatterbyte.shatterlib.client.config.EntryWidgetRegistry;
import it.hurts.shatterbyte.shatterlib.client.config.widget.CheckboxWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.GenericObjectWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.TextAreaWidget;
import it.hurts.shatterbyte.shatterlib.client.screen.TestGearScreen;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibClientCommand;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.MyClientConfig;
import it.hurts.shatterbyte.shatterlib.module.config.network.SyncServerConfigPacket;
import it.hurts.shatterbyte.shatterlib.module.config.network.TestScreenPacket;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import it.hurts.shatterbyte.shatterlib.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.shatterlib.util.DeltaTimeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

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
        EntryWidgetRegistry.register(Object.class, GenericObjectWidget::new);
        EntryWidgetRegistry.register(boolean.class, CheckboxWidget::new);
        EntryWidgetRegistry.register(Boolean.class, CheckboxWidget::new);
        EntryWidgetRegistry.register(String.class, TextAreaWidget::new);

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
