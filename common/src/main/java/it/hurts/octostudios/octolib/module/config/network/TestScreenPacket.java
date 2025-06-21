package it.hurts.octostudios.octolib.module.config.network;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.client.TestScreen;
import it.hurts.octostudios.octolib.client.screen.TestGearScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.InvocationTargetException;

public class TestScreenPacket implements CustomPacketPayload {
    public static final Type<TestScreenPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OctoLib.MODID, "test_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TestScreenPacket> STREAM_CODEC =
            CustomPacketPayload.codec(TestScreenPacket::write, TestScreenPacket::new);

    public TestScreenPacket(RegistryFriendlyByteBuf buf) {

    }

    public TestScreenPacket() {

    }
    
    public void write(RegistryFriendlyByteBuf buf) {

    }
    
    public void handle(NetworkManager.PacketContext packetContext) {
        Minecraft.getInstance().setScreen(new TestGearScreen());
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}
