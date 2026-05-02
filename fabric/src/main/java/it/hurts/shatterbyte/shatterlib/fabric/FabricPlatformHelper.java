package it.hurts.shatterbyte.shatterlib.fabric;

import it.hurts.shatterbyte.shatterlib.platform.ShatterLibPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class FabricPlatformHelper implements ShatterLibPlatform {
    public static final FabricPlatformHelper INSTANCE = new FabricPlatformHelper();
    private final Set<CustomPacketPayload.Type<?>> clientboundPayloads = new HashSet<>();
    private final Set<CustomPacketPayload.Type<?>> serverboundPayloads = new HashSet<>();

    private FabricPlatformHelper() {
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isClientEnvironment() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientboundPayload(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        if (clientboundPayloads.add(type)) {
            PayloadTypeRegistry.clientboundPlay().register(type, codec);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientboundReceiver(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ClientPayloadHandler<T> handler
    ) {
        registerClientboundPayload(type, codec);
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.handle(payload));
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerboundReceiver(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ServerPayloadHandler<T> handler
    ) {
        if (serverboundPayloads.add(type)) {
            PayloadTypeRegistry.serverboundPlay().register(type, codec);
        }
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.handle(payload, context.player()));
    }

    @Override
    public <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public <T extends CustomPacketPayload> void sendToPlayers(Collection<ServerPlayer> players, T payload) {
        for (ServerPlayer player : players) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}
