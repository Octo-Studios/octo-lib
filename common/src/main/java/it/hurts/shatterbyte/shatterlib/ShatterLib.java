package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibCommand;
import it.hurts.shatterbyte.shatterlib.module.config.Json5Utils;
import it.hurts.shatterbyte.shatterlib.module.config.MyConfig;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.AbstractEntry;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ShatterLib {
    public static final String MODID = "shatterlib";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static MyConfig CONFIG = new MyConfig();

    public static void init() {
        registerCommands();
        registerEvents();
        ShatterLibNetwork.init();
    }

    public static void main(String[] args) throws IllegalAccessException, IOException {
        //System.out.println("Hello World!\n\n\n\n\n\n\n\n\n"); // Display the string.
        CONFIG.load(Path.of("."));
        LOGGER.info(ShatterConfig.JSON5.serialize(Json5Utils.serializeObject(CONFIG)));
        //CONFIG.save(Path.of("."));
    }
    
    private static void registerEvents() {
        //PlayerEvent.PLAYER_JOIN.register(ConfigManager::syncConfigs);
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(ShatterLibCommand::register);
    }
}
