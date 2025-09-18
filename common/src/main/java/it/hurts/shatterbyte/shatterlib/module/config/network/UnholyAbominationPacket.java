package it.hurts.shatterbyte.shatterlib.module.config.network;

import dev.architectury.networking.NetworkManager;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.network.Packet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.lang.reflect.InvocationTargetException;

public class UnholyAbominationPacket extends Packet {
    public static final Type<UnholyAbominationPacket> TYPE =
            Packet.createType(ShatterLib.MODID, "no_just_no");
    public static final StreamCodec<RegistryFriendlyByteBuf, UnholyAbominationPacket> STREAM_CODEC =
            Packet.createCodec(UnholyAbominationPacket::write, UnholyAbominationPacket::new);

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

    @Override
    @Environment(EnvType.CLIENT)
    protected void handleClient(NetworkManager.PacketContext packetContext) {
        try {
            Class<?> clazz = Class.forName(path);
            var something = clazz.getConstructor().newInstance();
            if (something instanceof Screen screen) {
                Minecraft.getInstance().setScreen(screen);
            } else {
                ShatterLib.LOGGER.warn("ok that's not a screen, idk what it is, but not a screen: {}", path);
            }

        } catch (ClassNotFoundException e) {
            ShatterLib.LOGGER.warn("No class has been found: {}", path);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            ShatterLib.LOGGER.warn("no: {}", path);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

