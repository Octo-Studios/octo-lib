package it.hurts.shatterbyte.shatterlib.module.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
//import it.hurts.shatterbyte.shatterlib.module.config.network.SyncConfigPacket;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import it.hurts.shatterbyte.shatterlib.module.config.network.TestScreenPacket;
import net.fabricmc.api.EnvType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Supplier;

public class ShatterLibNetwork {
    //DeferredRegister<PacketPayloadInfo> register = DeferredRegister.create()

    public static <T extends CustomPacketPayload> void registerS2CPayloadType (
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec
    ) {
        if (Platform.getEnv() == EnvType.SERVER) {
            NetworkManager.registerS2CPayloadType(type, codec);
        }
    }

    public static <T extends CustomPacketPayload> void registerS2CReceiver (
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            NetworkManager.NetworkReceiver<T> receiver
    ) {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, type, codec, receiver);
    }

    public static <T extends CustomPacketPayload> void registerC2SReceiver (
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            NetworkManager.NetworkReceiver<T> receiver
    ) {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, type, codec, receiver);
    }
}
