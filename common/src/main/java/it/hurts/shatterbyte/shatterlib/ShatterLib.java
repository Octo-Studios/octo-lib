package it.hurts.shatterbyte.shatterlib;

import com.google.gson.Gson;
import de.marhali.json5.Json5;
import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Object;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import it.hurts.shatterbyte.shatterlib.client.shake.ShakeData;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibCommand;
//import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.MyConfig;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ShatterLib {
    
    public static final String MODID = "shatterlib";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static void init() {
        registerCommands();
        registerEvents();
        ShatterLibNetwork.init();
    }

    public static void main(String[] args) throws IOException {
        //System.out.println("Hello World!\n\n\n\n\n\n\n\n\n"); // Display the string.
        MyConfig cfg = new MyConfig();
        Path path = Paths.get("config.json5");
        cfg.load(path);
        System.out.println("Loaded: " + cfg.testValue + ", " + cfg.testRange + ", " + cfg.object.toString());
        // maybe change a value
        cfg.save(path);
    }
    
    private static void registerEvents() {
        //PlayerEvent.PLAYER_JOIN.register(ConfigManager::syncConfigs);
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(ShatterLibCommand::register);
    }
}
