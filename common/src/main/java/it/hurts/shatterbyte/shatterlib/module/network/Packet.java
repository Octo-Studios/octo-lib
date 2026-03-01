package it.hurts.shatterbyte.shatterlib.module.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public abstract class Packet implements CustomPacketPayload {
    protected Packet() {

    }

    public Packet(RegistryFriendlyByteBuf buf) {

    }

    public abstract void write(RegistryFriendlyByteBuf buf);


    public static <T extends Packet> Type<T> createType(String namespace, String path) {
        return new Type<>(Identifier.fromNamespaceAndPath(namespace, path));
    }

    public static <B extends ByteBuf, T extends Packet> StreamCodec<B, T> createCodec(StreamMemberEncoder<B, T> encoder, StreamDecoder<B, T> decoder) {
        return CustomPacketPayload.codec(encoder, decoder);
    }
}