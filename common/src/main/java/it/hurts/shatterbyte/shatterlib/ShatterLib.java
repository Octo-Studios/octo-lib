package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibCommand;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.dev.MyConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.ExampleConfig;
import it.hurts.shatterbyte.shatterlib.module.config.network.TestScreenPacket;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

import static it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork.registerS2C;

public final class ShatterLib {
    public static final String MODID = "shatterlib";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static MyConfig CONFIG = new MyConfig();
    public static ExampleConfig EXAMPLE_CONFIG = new ExampleConfig();

    public static void init() {
        registerCommands();
        registerEvents();

        registerS2C(TestScreenPacket.TYPE, TestScreenPacket.STREAM_CODEC, TestScreenPacket::handle);

        ConfigManager.register(CONFIG);

        LifecycleEvent.SETUP.register(() -> {
            if (Platform.getEnvironment() == Env.CLIENT) {
                LOGGER.info("Loading client configs");
                ConfigManager.getClientConfigs().forEach(config -> config.load(Platform.getConfigFolder()));
            }

            LOGGER.info("Loading common configs");
            ConfigManager.getCommonConfigs().forEach(config -> config.load(Platform.getConfigFolder()));
        });
    }

    public static void main(String[] args) {
        CONFIG.load(Path.of("."));
        EXAMPLE_CONFIG.load(Path.of("."));
    }
    
    private static void registerEvents() {
        PlayerEvent.PLAYER_JOIN.register(ConfigManager::syncConfigs);
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(ShatterLibCommand::register);
    }
}
