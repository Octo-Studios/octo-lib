package it.hurts.shatterbyte.shatterlib.platform;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;
import java.util.Collection;

public interface ShatterLibPlatform {
    boolean isDevelopmentEnvironment();

    boolean isClientEnvironment();

    Path getConfigDirectory();

    <T extends CustomPacketPayload> void registerClientboundPayload(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    );

    <T extends CustomPacketPayload> void registerClientboundReceiver(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ClientPayloadHandler<T> handler
    );

    <T extends CustomPacketPayload> void registerServerboundReceiver(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ServerPayloadHandler<T> handler
    );

    <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload);

    <T extends CustomPacketPayload> void sendToPlayers(Collection<ServerPlayer> players, T payload);

    @FunctionalInterface
    interface ClientPayloadHandler<T extends CustomPacketPayload> {
        void handle(T payload);
    }

    @FunctionalInterface
    interface ServerPayloadHandler<T extends CustomPacketPayload> {
        void handle(T payload, ServerPlayer player);
    }
}
