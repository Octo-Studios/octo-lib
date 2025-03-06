package it.hurts.octostudios.octolib.modules.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import io.netty.buffer.Unpooled;
import it.hurts.octostudios.octolib.modules.config.network.SyncConfigPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class OctolibNetwork {
    public static void init() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncConfigPacket.ID,
                    (buf, context) -> {
                        SyncConfigPacket packet = new SyncConfigPacket(buf);
                        packet.handle(context);
                    }
            );
        }
    }

    public static void sendSyncConfigPacket(ServerPlayer player, String configPath) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        SyncConfigPacket packet = new SyncConfigPacket(configPath);
        packet.write(buf);
        NetworkManager.sendToPlayer(player, SyncConfigPacket.ID, buf);
    }
}