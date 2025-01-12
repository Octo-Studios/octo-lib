package it.hurts.octostudios.octolib.modules.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import it.hurts.octostudios.octolib.modules.config.network.SyncConfigPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class OctolibNetwork {
    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.s2c(), SyncConfigPacket.ID, (buf, context) -> {
            SyncConfigPacket packet = new SyncConfigPacket(buf);
            packet.handle(context);
        });
    }

    public static void sendSyncConfigPacket(ServerPlayer player, String configPath) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(configPath);
        buf.writeUtf(ConfigManager.saveAsString(configPath));

        NetworkManager.sendToPlayer(player, SyncConfigPacket.ID, buf);
    }
}