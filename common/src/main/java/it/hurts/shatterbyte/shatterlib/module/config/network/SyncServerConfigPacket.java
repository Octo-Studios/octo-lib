package it.hurts.shatterbyte.shatterlib.module.config.network;

import de.marhali.json5.Json5;
import dev.architectury.networking.NetworkManager;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.util.Json5Utils;
import it.hurts.shatterbyte.shatterlib.module.network.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class SyncServerConfigPacket extends Packet {
    public static Type<SyncServerConfigPacket> TYPE = Packet.createType(ShatterLib.MODID, "sync_server_config");
    public static StreamCodec<RegistryFriendlyByteBuf, SyncServerConfigPacket> STREAM_CODEC = Packet.createCodec(SyncServerConfigPacket::write, SyncServerConfigPacket::new);

    private String path;
    private String json;

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
    protected void handleClient(NetworkManager.PacketContext packetContext) {
        ShatterConfig config = ConfigManager.getConfig(path);
        if (config == null) {
            return;
        }

        config.loadFromJson(json);
        config.updateSchemaCache();

        packetContext.getPlayer().displayClientMessage(Component.literal("Recieved a sync packet! Path: "+path+". Contents: "+json), false);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
