package it.hurts.octostudios.octolib;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import it.hurts.octostudios.octolib.modules.ConfigTest;
import it.hurts.octostudios.octolib.modules.commands.OctolibCommand;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import it.hurts.octostudios.octolib.modules.config.event.ConfigJoinEvent;
import it.hurts.octostudios.octolib.modules.network.OctolibNetwork;
import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class OctoLib {
    
    public static final String MODID = "octolib";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static void init() {
        registerCommands();
        registerEvents();
        OctolibNetwork.init();
        
        ConfigManager.registerConfigPackage(ConfigTest.class, "octotest");
    }
    
    private static void registerEvents() {
        PlayerEvent.PLAYER_JOIN.register(new ConfigJoinEvent());
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(OctolibCommand::register);
    }
}
