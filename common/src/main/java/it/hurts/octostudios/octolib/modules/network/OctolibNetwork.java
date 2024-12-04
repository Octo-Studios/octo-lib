package it.hurts.octostudios.octolib.modules.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import it.hurts.octostudios.octolib.modules.config.network.SyncConfigPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class OctolibNetwork {
    
    public static void init() {
        registerS2C(SyncConfigPacket.TYPE, SyncConfigPacket.STREAM_CODEC, SyncConfigPacket::handle);
    }
    
    private static  <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            NetworkManager.NetworkReceiver<T> receiver) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), type, codec, receiver);
        } else {
            NetworkManager.registerS2CPayloadType(type, codec);
        }
    }
    
}
