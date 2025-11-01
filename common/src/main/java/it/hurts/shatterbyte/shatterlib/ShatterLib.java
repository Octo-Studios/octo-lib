package it.hurts.shatterbyte.shatterlib;

import de.marhali.json5.Json5Element;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibCommand;
import it.hurts.shatterbyte.shatterlib.module.config.Json5Utils;
import it.hurts.shatterbyte.shatterlib.module.config.MyConfig;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.ExampleConfig;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public final class ShatterLib {
    public static final String MODID = "shatterlib";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static MyConfig CONFIG = new MyConfig();
    public static ExampleConfig EXAMPLE_CONFIG = new ExampleConfig();

    public static void init() {
        registerCommands();
        registerEvents();
        ShatterLibNetwork.init();
    }

    public static void main(String[] args) throws IllegalAccessException, IOException {
        //System.out.println("Hello World!\n\n\n\n\n\n\n\n\n"); // Display the string.
        CONFIG.load(Path.of("."));
//        LOGGER.info(ShatterConfig.JSON5.serialize(Json5Utils.encode(CONFIG)));
//
//        Optional<Json5Element> defaultColorJson = CONFIG.getDefaultElement("colorMap");
        EXAMPLE_CONFIG.load(Path.of("."));
        //LOGGER.info(ShatterConfig.JSON5.serialize(Json5Utils.encode(EXAMPLE_CONFIG)));
        //CONFIG.save(Path.of("."));
    }
    
    private static void registerEvents() {
        //PlayerEvent.PLAYER_JOIN.register(ConfigManager::syncConfigs);
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(ShatterLibCommand::register);
    }
}
