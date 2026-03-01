package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibCommand;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.network.SyncConfigPacket;
import it.hurts.shatterbyte.shatterlib.module.config.network.TestScreenPacket;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ShatterLib {
    
    public static final String MODID = "shatterlib";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static void init() {
        registerCommands();
        registerEvents();

        ShatterLibNetwork.registerS2CPayloadType(TestScreenPacket.TYPE, TestScreenPacket.STREAM_CODEC);
        ShatterLibNetwork.registerS2CPayloadType(SyncConfigPacket.TYPE, SyncConfigPacket.STREAM_CODEC);
    }
    
    private static void registerEvents() {
        PlayerEvent.PLAYER_JOIN.register(ConfigManager::syncConfigs);
    }
    
    private static void registerCommands() {
        CommandRegistrationEvent.EVENT.register(ShatterLibCommand::register);
    }
}
