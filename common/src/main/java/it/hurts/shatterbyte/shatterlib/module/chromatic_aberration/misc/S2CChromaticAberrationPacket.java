package it.hurts.shatterbyte.shatterlib.module.chromatic_aberration.misc;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.chromatic_aberration.ChromaticAberration;
import it.hurts.shatterbyte.shatterlib.module.network.Packet;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Getter
public class S2CChromaticAberrationPacket extends Packet {
    public static final Type<S2CChromaticAberrationPacket> TYPE = Packet.createType(ShatterLib.MOD_ID, "chromatic_aberration");
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CChromaticAberrationPacket> STREAM_CODEC = Packet.createCodec(S2CChromaticAberrationPacket::write, S2CChromaticAberrationPacket::new);

    private final ChromaticAberration chromaticAberration;

    public S2CChromaticAberrationPacket(ChromaticAberration chromaticAberration) {
        this.chromaticAberration = chromaticAberration;
    }

    public S2CChromaticAberrationPacket(RegistryFriendlyByteBuf buf) {
        super(buf);

        this.chromaticAberration = ChromaticAberration.STREAM_CODEC.decode(buf);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        ChromaticAberration.STREAM_CODEC.encode(buf, chromaticAberration);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
