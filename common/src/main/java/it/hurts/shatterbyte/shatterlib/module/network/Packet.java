package it.hurts.shatterbyte.shatterlib.module.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public abstract class Packet implements CustomPacketPayload {
    protected Packet() {

    }

    public Packet(RegistryFriendlyByteBuf buf) {

    }

    public abstract void write(RegistryFriendlyByteBuf buf);

    public final void handle(NetworkManager.PacketContext packetContext) {
        switch (packetContext.getEnvironment()) {
            case CLIENT -> this.handleClient(packetContext);
            case SERVER -> this.handleServer(packetContext);
        }
    }

    @Environment(EnvType.CLIENT)
    protected void handleClient(NetworkManager.PacketContext packetContext) {

    }

    protected void handleServer(NetworkManager.PacketContext packetContext) {

    }

    public static <T extends Packet> Type<T> createType(String namespace, String path) {
        return new Type<>(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    public static <B extends ByteBuf, T extends Packet> StreamCodec<B, T> createCodec(StreamMemberEncoder<B, T> encoder, StreamDecoder<B, T> decoder) {
        return CustomPacketPayload.codec(encoder, decoder);
    }
}