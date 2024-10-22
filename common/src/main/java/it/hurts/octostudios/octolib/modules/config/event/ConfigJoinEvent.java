package it.hurts.octostudios.octolib.modules.config.event;

import dev.architectury.event.events.common.PlayerEvent;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import it.hurts.octostudios.octolib.modules.config.network.SyncConfigPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class ConfigJoinEvent implements PlayerEvent.PlayerJoin {
    
    @Override
    public void join(ServerPlayerEntity player) {
        ConfigManager.getServerConfigs().forEach(s -> new SyncConfigPacket(s).sendTo(player));
    }
    
}
