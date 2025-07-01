package it.hurts.octostudios.octolib.module.config.network;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.module.config.ConfigManager;
import it.hurts.octostudios.octolib.module.network.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class SyncConfigPacket extends Packet {
    public static final CustomPacketPayload.Type<SyncConfigPacket> TYPE =
            Packet.createType(OctoLib.MODID, "config_sync");
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncConfigPacket> STREAM_CODEC =
            Packet.createCodec(SyncConfigPacket::write, SyncConfigPacket::new);

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
    
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(configPath);
        buf.writeUtf(configFile);
    }

    @Override
    protected void handleClient(NetworkManager.PacketContext packetContext) {
        ConfigManager.reloadStringConfig(configFile, configPath, false);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
