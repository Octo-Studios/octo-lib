package it.hurts.octostudios.octolib.module.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import it.hurts.octostudios.octolib.module.config.network.SyncConfigPacket;
import it.hurts.octostudios.octolib.module.config.network.TestScreenPacket;
import it.hurts.octostudios.octolib.module.config.network.UnholyAbominationPacket;
import net.fabricmc.api.EnvType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class OctolibNetwork {
    public static void init() {
        if (Platform.getEnv() == EnvType.SERVER) {
            NetworkManager.registerS2CPayloadType(SyncConfigPacket.TYPE, SyncConfigPacket.STREAM_CODEC);
            NetworkManager.registerS2CPayloadType(TestScreenPacket.TYPE, TestScreenPacket.STREAM_CODEC);
            NetworkManager.registerS2CPayloadType(UnholyAbominationPacket.TYPE, UnholyAbominationPacket.STREAM_CODEC);
        }
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
