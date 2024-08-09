package it.hurts.octostudios.octolib;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import it.hurts.octostudios.octolib.modules.commands.OctolibCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class OctoLib {
    public static final String MODID = "octolib";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static void init() {
        registerCommands();
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(OctolibCommand::register);
    }
}
