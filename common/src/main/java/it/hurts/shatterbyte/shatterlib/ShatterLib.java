package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibCommand;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.dev.ExampleConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.MyConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.TestServerConfig;
import it.hurts.shatterbyte.shatterlib.module.config.network.SyncServerConfigPacket;
import it.hurts.shatterbyte.shatterlib.module.config.network.TestScreenPacket;
import it.hurts.shatterbyte.shatterlib.module.config.util.Json5Utils;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public final class ShatterLib {
    public static final String MOD_ID = "shatterlib";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static MyConfig CONFIG = new MyConfig();
    public static TestServerConfig SERVER_CONFIG = new TestServerConfig();
    public static ExampleConfig EXAMPLE = new ExampleConfig();

    public static void init() {
        registerCommands();
        registerEvents();

        ShatterLibNetwork.registerS2CPayloadType(TestScreenPacket.TYPE, TestScreenPacket.STREAM_CODEC);
        ShatterLibNetwork.registerS2CPayloadType(SyncServerConfigPacket.TYPE, SyncServerConfigPacket.STREAM_CODEC);

        if (Platform.isDevelopmentEnvironment()) {
            ConfigManager.register(MOD_ID, CONFIG);
            ConfigManager.register(MOD_ID, SERVER_CONFIG);
            ConfigManager.register(MOD_ID, EXAMPLE);
        }

        LifecycleEvent.SETUP.register(() -> {
            if (Platform.getEnvironment() == Env.CLIENT) {
                LOGGER.info("Loading client configs");
                ConfigManager.loadAllClientConfigs();
            }

            LOGGER.info("Loading common configs");
            ConfigManager.loadAllCommonConfigs();

            LOGGER.info("Loading server configs");
            ConfigManager.loadAllServerConfigs();
        });

        LifecycleEvent.SERVER_BEFORE_START.register(serverState -> {
            Path serverConfigFolder = serverState.getWorldPath(ConfigManager.SERVER_CONFIG);

            LOGGER.info("Loading server config overrides");
            ConfigManager.loadServerConfigOverrides(serverConfigFolder);
        });
    }
    
    private static void registerEvents() {
        PlayerEvent.PLAYER_JOIN.register(ConfigManager::syncServerConfigs);
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(ShatterLibCommand::register);
    }
}
