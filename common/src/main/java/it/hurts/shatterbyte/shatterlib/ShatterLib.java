package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibCommand;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ShatterLib {
    
    public static final String MODID = "shatterlib";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static void init() {
        registerCommands();
        registerEvents();
        ShatterLibNetwork.init();
    }
    
    private static void registerEvents() {
        PlayerEvent.PLAYER_JOIN.register(ConfigManager::syncConfigs);
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(ShatterLibCommand::register);
    }
}
