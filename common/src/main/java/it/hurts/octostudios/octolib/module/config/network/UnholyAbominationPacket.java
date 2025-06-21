package it.hurts.octostudios.octolib.module.config.network;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.client.screen.TestGearScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.InvocationTargetException;

public class UnholyAbominationPacket implements CustomPacketPayload {
    public static final Type<UnholyAbominationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OctoLib.MODID, "no_just_no"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UnholyAbominationPacket> STREAM_CODEC =
            CustomPacketPayload.codec(UnholyAbominationPacket::write, UnholyAbominationPacket::new);

    String path;

    public UnholyAbominationPacket(RegistryFriendlyByteBuf buf) {
        this.path = buf.readUtf();
    }

    public UnholyAbominationPacket(String path) {
        this.path = path;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(path);
    }

    public void handle(NetworkManager.PacketContext packetContext) {
        try {
            Class<?> clazz = Class.forName(path);
            var something = clazz.getConstructor().newInstance();
            if (something instanceof Screen screen) {
                Minecraft.getInstance().setScreen(screen);
            } else {
                OctoLib.LOGGER.warn("ok that's not a screen, idk what it is, but not a screen: {}", path);
            }

        } catch (ClassNotFoundException e) {
            OctoLib.LOGGER.warn("No class has been found: {}", path);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            OctoLib.LOGGER.warn("no: {}", path);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}

