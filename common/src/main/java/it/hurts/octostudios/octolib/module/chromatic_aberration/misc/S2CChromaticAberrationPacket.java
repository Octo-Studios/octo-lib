package it.hurts.octostudios.octolib.module.chromatic_aberration.misc;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.module.chromatic_aberration.ChromaticAberration;
import it.hurts.octostudios.octolib.module.chromatic_aberration.ChromaticAberrationManager;
import it.hurts.octostudios.octolib.module.network.Packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class S2CChromaticAberrationPacket extends Packet {
    public static final Type<S2CChromaticAberrationPacket> TYPE = Packet.createType(OctoLib.MODID, "chromatic_aberration");
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CChromaticAberrationPacket> STREAM_CODEC = Packet.createCodec(S2CChromaticAberrationPacket::write, S2CChromaticAberrationPacket::new);

    private ChromaticAberration chromaticAberration;

    public S2CChromaticAberrationPacket(RegistryFriendlyByteBuf buf) {
        super(buf);

        this.chromaticAberration = ChromaticAberration.STREAM_CODEC.decode(buf);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        ChromaticAberration.STREAM_CODEC.encode(buf, chromaticAberration);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void handleClient(NetworkManager.PacketContext packetContext) {
        ChromaticAberrationManager.add(packetContext.getPlayer().level(), this.getChromaticAberration());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
