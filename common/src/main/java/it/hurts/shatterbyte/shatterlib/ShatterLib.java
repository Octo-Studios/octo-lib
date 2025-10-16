package it.hurts.shatterbyte.shatterlib;

import com.google.gson.Gson;
import de.marhali.json5.Json5;
import de.marhali.json5.Json5Object;
import de.marhali.json5.config.Json5Options;
import de.marhali.json5.stream.Json5Lexer;
import de.marhali.json5.stream.Json5Parser;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import it.hurts.shatterbyte.shatterlib.client.shake.ShakeData;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibCommand;
//import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
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

    public static void main(String[] args) {
        //System.out.println("Hello World!\n\n\n\n\n\n\n\n\n"); // Display the string.
        ShakeData testObject = new ShakeData(1, 1, 1);

        Json5 json5 = Json5.builder(builder -> builder
                .quoteless()
                .writeComments()
                .prettyPrinting()
                .build());

        String json = new Gson().toJson(testObject);
        System.out.println("\nRegular Gson.toJson(): \n"+json);
        System.out.println("\nGson.toJson() output put in Json5.parse(): \n"+json5.parse(json));
    }
    
    private static void registerEvents() {
        //PlayerEvent.PLAYER_JOIN.register(ConfigManager::syncConfigs);
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(ShatterLibCommand::register);
    }
}
