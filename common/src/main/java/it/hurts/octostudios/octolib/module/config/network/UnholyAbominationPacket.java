package it.hurts.octostudios.octolib.module.config.network;

import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.module.network.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class UnholyAbominationPacket extends Packet {
    public static final Type<UnholyAbominationPacket> TYPE =
            Packet.createType(OctoLib.MODID, "no_just_no");
    public static final StreamCodec<RegistryFriendlyByteBuf, UnholyAbominationPacket> STREAM_CODEC =
            Packet.createCodec(UnholyAbominationPacket::write, UnholyAbominationPacket::new);

    private final String path;

    public UnholyAbominationPacket(RegistryFriendlyByteBuf buf) {
        this.path = buf.readUtf();
    }

    public UnholyAbominationPacket(String path) {
        this.path = path;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(path);
    }

    public String getPath() {
        return path;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

