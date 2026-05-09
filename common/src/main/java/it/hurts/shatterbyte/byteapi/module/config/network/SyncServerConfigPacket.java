package it.hurts.shatterbyte.byteapi.module.config.network;

import it.hurts.shatterbyte.byteapi.ByteAPI;
import it.hurts.shatterbyte.byteapi.module.config.ShatterConfig;
import it.hurts.shatterbyte.byteapi.module.network.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class SyncServerConfigPacket extends Packet {
    public static Type<SyncServerConfigPacket> TYPE = Packet.createType(ByteAPI.MOD_ID, "sync_server_config");
    public static StreamCodec<RegistryFriendlyByteBuf, SyncServerConfigPacket> STREAM_CODEC = Packet.createCodec(SyncServerConfigPacket::write, SyncServerConfigPacket::new);

    public String path;
    public String json;

    public SyncServerConfigPacket(RegistryFriendlyByteBuf buf) {
        this.path = buf.readUtf();
        this.json = buf.readUtf(99999);
    }

    public SyncServerConfigPacket(ShatterConfig serverConfig) {
        this.path = serverConfig.getPath();
        this.json = serverConfig.getCurrentSchema().toString();
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(path);
        buf.writeUtf(json, 99999);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
