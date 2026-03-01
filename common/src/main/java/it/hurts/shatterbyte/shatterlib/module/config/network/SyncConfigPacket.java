package it.hurts.shatterbyte.shatterlib.module.config.network;

import dev.architectury.networking.NetworkManager;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.network.Packet;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Getter
public class SyncConfigPacket extends Packet {
    public static final CustomPacketPayload.Type<SyncConfigPacket> TYPE =
            Packet.createType(ShatterLib.MODID, "config_sync");
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
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
