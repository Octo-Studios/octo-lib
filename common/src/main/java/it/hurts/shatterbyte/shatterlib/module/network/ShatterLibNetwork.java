package it.hurts.shatterbyte.shatterlib.module.network;

import it.hurts.shatterbyte.shatterlib.platform.ShatterLibPlatform;
import it.hurts.shatterbyte.shatterlib.platform.ShatterLibServices;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class ShatterLibNetwork {
    public static <T extends CustomPacketPayload> void registerS2CPayloadType (
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        ShatterLibServices.platform().registerClientboundPayload(type, codec);
    }

    public static <T extends CustomPacketPayload> void registerS2CReceiver (
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ShatterLibPlatform.ClientPayloadHandler<T> receiver
    ) {
        ShatterLibServices.platform().registerClientboundReceiver(type, codec, receiver);
    }

    public static <T extends CustomPacketPayload> void registerC2SReceiver (
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ShatterLibPlatform.ServerPayloadHandler<T> receiver
    ) {
        ShatterLibServices.platform().registerServerboundReceiver(type, codec, receiver);
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
        ShatterLibServices.platform().sendToPlayer(player, payload);
    }

    public static <T extends CustomPacketPayload> void sendToPlayers(Collection<ServerPlayer> players, T payload) {
        ShatterLibServices.platform().sendToPlayers(players, payload);
    }
}
