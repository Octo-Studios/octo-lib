package it.hurts.shatterbyte.shatterlib;

import com.google.gson.Gson;
import de.marhali.json5.Json5;
import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Object;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import it.hurts.shatterbyte.shatterlib.client.shake.ShakeData;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibCommand;
//import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.MyConfig;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
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

        try {
            ShatterLib.main(new String[]{});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        //System.out.println("Hello World!\n\n\n\n\n\n\n\n\n"); // Display the string.
        MyConfig cfg = new MyConfig();
        ConfigManager.register(cfg);

        for (ShatterConfig config : ConfigManager.getRegisteredConfigs()) {
            Path path = Paths.get(config.getPath() + ".json5");
            config.load(path);
            config.save(path);
        }
    }
    
    private static void registerEvents() {
        //PlayerEvent.PLAYER_JOIN.register(ConfigManager::syncConfigs);
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(ShatterLibCommand::register);
    }
}
