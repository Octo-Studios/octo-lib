package it.hurts.octostudios.octolib;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import it.hurts.octostudios.octolib.client.animation.TweenSystem;
import it.hurts.octostudios.octolib.client.screen.TestGearScreen;
import it.hurts.octostudios.octolib.module.config.network.SyncConfigPacket;
import it.hurts.octostudios.octolib.module.config.network.TestScreenPacket;
import it.hurts.octostudios.octolib.module.config.network.UnholyAbominationPacket;
import it.hurts.octostudios.octolib.module.network.OctolibNetwork;
import it.hurts.octostudios.octolib.module.particle.OctoRenderManager;
import it.hurts.octostudios.octolib.util.DeltaTimeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.InvocationTargetException;

public final class OctoLibClient {
    public static void init() {
        registerEvents();
        registerPacketReceivers();

        TweenSystem.init();
    }
    
    private static void registerEvents() {
        ClientTickEvent.CLIENT_LEVEL_PRE.register(OctoRenderManager::clientTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(OctoRenderManager::worldExit);
    }

    private static void registerPacketReceivers() {
        OctolibNetwork.registerS2C(SyncConfigPacket.TYPE, SyncConfigPacket.STREAM_CODEC, SyncConfigPacket::handle);
        OctolibNetwork.registerS2C(TestScreenPacket.TYPE, TestScreenPacket.STREAM_CODEC,
                (packet, context) -> Minecraft.getInstance().setScreen(new TestGearScreen()));
        OctolibNetwork.registerS2C(UnholyAbominationPacket.TYPE, UnholyAbominationPacket.STREAM_CODEC,
                (packet, context) -> openScreen(packet.getPath()));
    }

    private static void openScreen(String path) {
        try {
            var screenClass = Class.forName(path);
            var instance = screenClass.getConstructor().newInstance();
            if (instance instanceof Screen screen) {
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

    public static double getDeltaTime() {
        return DeltaTimeTracker.getDeltaSeconds();
    }
}
