package it.hurts.shatterbyte.shatterlib.module.config.network;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.network.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class TestScreenPacket extends Packet {
    public static Type<TestScreenPacket> TYPE = Packet.createType(ShatterLib.MODID, "test_screen");
    public static StreamCodec<RegistryFriendlyByteBuf, TestScreenPacket> STREAM_CODEC = Packet.createCodec(TestScreenPacket::write, TestScreenPacket::new);

    public TestScreenPacket(RegistryFriendlyByteBuf buf) {
        super(buf);
    }

    public TestScreenPacket() {

    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}