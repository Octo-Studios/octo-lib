package it.hurts.octostudios.octolib;

import dev.architectury.event.events.common.LifecycleEvent;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class OctoLib {
    public static final String MODID = "octolib";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static void init() {
        LifecycleEvent.SETUP.register(ConfigManager::reloadAll);
    }
}
