package it.hurts.shatterbyte.shatterlib.module.camera_shake.misc;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.network.Packet;
import it.hurts.shatterbyte.shatterlib.module.camera_shake.CameraShake;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Getter
public class S2CCameraShakePacket extends Packet {
    public static final Type<S2CCameraShakePacket> TYPE = Packet.createType(ShatterLib.MOD_ID, "camera_shake");
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CCameraShakePacket> STREAM_CODEC = Packet.createCodec(S2CCameraShakePacket::write, S2CCameraShakePacket::new);

    private final CameraShake shake;

    public S2CCameraShakePacket(CameraShake shake) {
        this.shake = shake;
    }

    public S2CCameraShakePacket(RegistryFriendlyByteBuf buf) {
        super(buf);

        this.shake = CameraShake.STREAM_CODEC.decode(buf);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        CameraShake.STREAM_CODEC.encode(buf, shake);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
