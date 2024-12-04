package it.hurts.octostudios.octolib.modules.config.network;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class SyncConfigPacket implements CustomPacketPayload {
    
    private final String configPath;
    private final String configFile;
    
    public static final CustomPacketPayload.Type<SyncConfigPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(OctoLib.MODID, "altar_multiplier_sync"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncConfigPacket> STREAM_CODEC =
            CustomPacketPayload.codec(SyncConfigPacket::write, SyncConfigPacket::new);
    
    public SyncConfigPacket(RegistryFriendlyByteBuf buf) {
        this.configPath = buf.readUtf();
        this.configFile = buf.readUtf();
    }
    
    public SyncConfigPacket(String configPath) {
        this.configPath = configPath;
        this.configFile = ConfigManager.saveAsString(configPath);
    }
    
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(configPath);
        buf.writeUtf(configFile);
    }
    
    public void handle(NetworkManager.PacketContext packetContext) {
        ConfigManager.reloadStringConfig(configFile, configPath, false);
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}
