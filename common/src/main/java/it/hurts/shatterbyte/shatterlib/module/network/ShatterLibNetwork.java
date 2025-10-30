package it.hurts.shatterbyte.shatterlib.module.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
//import it.hurts.shatterbyte.shatterlib.module.config.network.SyncConfigPacket;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import it.hurts.shatterbyte.shatterlib.module.config.network.TestScreenPacket;
import net.fabricmc.api.EnvType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Supplier;

public class ShatterLibNetwork {
    public static void init() {
        registerS2C(TestScreenPacket.TYPE, TestScreenPacket.STREAM_CODEC, TestScreenPacket::handle);
    }

    public static <T extends CustomPacketPayload> void registerS2C (
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            NetworkManager.NetworkReceiver<T> receiver
    ) {
        if (Platform.getEnv() == EnvType.SERVER) {
            NetworkManager.registerS2CPayloadType(type, codec);
        } else {
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, type, codec, receiver);
        }
    }

    public static <T extends CustomPacketPayload> void registerC2S (
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            NetworkManager.NetworkReceiver<T> receiver
    ) {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, type, codec, receiver);
    }
}
