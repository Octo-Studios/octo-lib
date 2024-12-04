package it.hurts.octostudios.octolib.modules.config.event;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import it.hurts.octostudios.octolib.modules.config.network.SyncConfigPacket;
import net.minecraft.server.level.ServerPlayer;

public class ConfigJoinEvent implements PlayerEvent.PlayerJoin {
    
    @Override
    public void join(ServerPlayer player) {
        ConfigManager.syncConfigs(player);
    }
    
}
