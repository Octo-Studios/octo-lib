package it.hurts.octostudios.octolib;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import it.hurts.octostudios.octolib.modules.ConfigTest;
import it.hurts.octostudios.octolib.modules.commands.OctolibCommand;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import it.hurts.octostudios.octolib.modules.config.event.ConfigJoinEvent;
import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class OctoLibClient {
    public static void init() {
        registerEvents();
    }
    
    private static void registerEvents() {
        ClientTickEvent.CLIENT_LEVEL_PRE.register(OctoRenderManager::clientTick);
    }
}
