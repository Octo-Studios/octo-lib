package it.hurts.shatterbyte.byteapi.neoforge;

import it.hurts.shatterbyte.byteapi.ByteAPI;
import it.hurts.shatterbyte.byteapi.platform.ByteAPIPlatform;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class NeoForgePlatformHelper implements ByteAPIPlatform {
    private final List<ClientboundPayloadRegistration<?>> clientboundPayloads = new ArrayList<>();
    private final List<ClientboundReceiverRegistration<?>> clientboundReceivers = new ArrayList<>();
    private final List<ServerboundReceiverRegistration<?>> serverboundReceivers = new ArrayList<>();

    public NeoForgePlatformHelper(IEventBus modBus) {
        modBus.addListener(this::registerPayloadHandlers);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public boolean isClientEnvironment() {
        return FMLLoader.getCurrent().getDist() == Dist.CLIENT;
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientboundPayload(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        clientboundPayloads.add(new ClientboundPayloadRegistration<>(type, codec));
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientboundReceiver(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ClientPayloadHandler<T> handler
    ) {
        clientboundReceivers.add(new ClientboundReceiverRegistration<>(type, codec, handler));
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerboundReceiver(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ServerPayloadHandler<T> handler
    ) {
        serverboundReceivers.add(new ServerboundReceiverRegistration<>(type, codec, handler));
    }

    @Override
    public <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public <T extends CustomPacketPayload> void sendToPlayers(Collection<ServerPlayer> players, T payload) {
        for (ServerPlayer player : players) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }

    private void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(ByteAPI.MOD_ID).versioned("1");

        for (ClientboundReceiverRegistration<?> registration : clientboundReceivers) {
            registration.register(registrar);
        }

        for (ClientboundPayloadRegistration<?> registration : clientboundPayloads) {
            if (clientboundReceivers.stream().noneMatch(receiver -> receiver.type().id().equals(registration.type().id()))) {
                registration.register(registrar);
            }
        }

        for (ServerboundReceiverRegistration<?> registration : serverboundReceivers) {
            registration.register(registrar);
        }
    }

    private record ClientboundPayloadRegistration<T extends CustomPacketPayload>(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        private void register(PayloadRegistrar registrar) {
            registrar.playToClient(type, codec, (payload, context) -> {
            });
        }
    }

    private record ClientboundReceiverRegistration<T extends CustomPacketPayload>(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ClientPayloadHandler<T> handler
    ) {
        private void register(PayloadRegistrar registrar) {
            registrar.playToClient(type, codec, (payload, context) -> context.enqueueWork(() -> handler.handle(payload)));
        }
    }

    private record ServerboundReceiverRegistration<T extends CustomPacketPayload>(
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            ServerPayloadHandler<T> handler
    ) {
        private void register(PayloadRegistrar registrar) {
            registrar.playToServer(type, codec, (payload, context) -> context.enqueueWork(() -> handler.handle(payload, (ServerPlayer) context.player())));
        }
    }
}
