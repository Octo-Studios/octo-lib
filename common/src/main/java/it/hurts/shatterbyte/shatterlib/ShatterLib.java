package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibCommand;
import it.hurts.shatterbyte.shatterlib.module.config.MyConfig;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public final class ShatterLib {
    public static final String MODID = "shatterlib";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static MyConfig CONFIG = new MyConfig();

    public static void init() {
        registerCommands();
        registerEvents();
        ShatterLibNetwork.init();
    }

    public static void main(String[] args) throws IOException {
        //System.out.println("Hello World!\n\n\n\n\n\n\n\n\n"); // Display the string.
        MyConfig cfg = new MyConfig();
        cfg.getTestColor();
    }
    
    private static void registerEvents() {
        //PlayerEvent.PLAYER_JOIN.register(ConfigManager::syncConfigs);
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(ShatterLibCommand::register);
    }
}
