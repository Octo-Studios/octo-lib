package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibCommand;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.dev.MyConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.ExampleConfig;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public final class ShatterLib {
    public static final String MODID = "shatterlib";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static MyConfig CONFIG = new MyConfig();
    public static ExampleConfig EXAMPLE_CONFIG = new ExampleConfig();

    public static void init() {
        registerCommands();
        registerEvents();
        ShatterLibNetwork.init();
        ConfigManager.registerCommon(CONFIG);
        CONFIG.load();
    }

    public static void main(String[] args) throws IllegalAccessException, IOException {
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
