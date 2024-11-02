package it.hurts.octostudios.octolib.modules.config.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import net.minecraft.network.RegistryFriendlyByteBuf;

import static it.hurts.octostudios.octolib.modules.network.OctolibNetwork.SYNC_SHOP;

public class SyncConfigPacket extends BaseS2CMessage {
    
    private final String configPath;
    private final String configFile;
    
    public SyncConfigPacket(RegistryFriendlyByteBuf buf) {
        this.configPath = buf.readUtf();
        this.configFile = buf.readUtf();
    }
    
    public SyncConfigPacket(String configPath) {
        this.configPath = configPath;
        this.configFile = ConfigManager.saveAsString(configPath);
    }
    
    @Override
    public MessageType getType() {
        return SYNC_SHOP;
    }
    
    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(configPath);
        buf.writeUtf(configFile);
    }
    
    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        ConfigManager.reloadStringConfig(configFile, configPath, false);
    }
    
}
