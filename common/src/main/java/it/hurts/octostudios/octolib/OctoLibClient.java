package it.hurts.octostudios.octolib;

import dev.architectury.event.events.client.ClientTickEvent;
import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;

public final class OctoLibClient {
    public static void init() {
        registerEvents();
    }
    
    private static void registerEvents() {
        ClientTickEvent.CLIENT_LEVEL_PRE.register(OctoRenderManager::clientTick);
    }
}
