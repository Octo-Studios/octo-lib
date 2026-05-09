package it.hurts.shatterbyte.byteapi.module.network;

import it.hurts.shatterbyte.byteapi.platform.ByteAPIPlatform;
import it.hurts.shatterbyte.byteapi.platform.ByteAPIServices;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class ByteAPINetwork {
    public static <T extends CustomPacketPayload> void registerS2CPayloadType (
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        ByteAPIServices.platform().registerClientboundPayload(type, codec);
    }

    public static <T extends CustomPacketPayload> void registerS2CReceiver (
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ByteAPIPlatform.ClientPayloadHandler<T> receiver
    ) {
        ByteAPIServices.platform().registerClientboundReceiver(type, codec, receiver);
    }

    public static <T extends CustomPacketPayload> void registerC2SReceiver (
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ByteAPIPlatform.ServerPayloadHandler<T> receiver
    ) {
        ByteAPIServices.platform().registerServerboundReceiver(type, codec, receiver);
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
        ByteAPIServices.platform().sendToPlayer(player, payload);
    }

    public static <T extends CustomPacketPayload> void sendToPlayers(Collection<ServerPlayer> players, T payload) {
        ByteAPIServices.platform().sendToPlayers(players, payload);
    }
}
