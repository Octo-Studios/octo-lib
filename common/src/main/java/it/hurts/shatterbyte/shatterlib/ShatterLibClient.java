package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import it.hurts.shatterbyte.shatterlib.client.animation.TweenSystem;
import it.hurts.shatterbyte.shatterlib.client.screen.TestGearScreen;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.impl.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.network.SyncConfigPacket;
import it.hurts.shatterbyte.shatterlib.module.config.network.TestScreenPacket;
import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import it.hurts.shatterbyte.shatterlib.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.shatterlib.module.particle.trail.EntityTrailRegistry;
import it.hurts.shatterbyte.shatterlib.module.particle.trail.TestArrowTrail;
import it.hurts.shatterbyte.shatterlib.util.DeltaTimeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;

public final class ShatterLibClient {
    public static void init() {
        registerEvents();
        registerPacketReceivers();

        TweenSystem.init();
        //EntityTrailRegistry.registerProvider(EntityType.ARROW, TestArrowTrail::new);
    }
    
    private static void registerEvents() {
        ClientTickEvent.CLIENT_LEVEL_PRE.register(ShatterRenderManager::clientTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(ShatterRenderManager::worldExit);
    }

    private static void registerPacketReceivers() {
        ShatterLibNetwork.registerS2CReceiver(TestScreenPacket.TYPE, TestScreenPacket.STREAM_CODEC, (value, context) -> {
            Minecraft.getInstance().setScreen(new TestGearScreen());
        });

        ShatterLibNetwork.registerS2CReceiver(SyncConfigPacket.TYPE, SyncConfigPacket.STREAM_CODEC, (value, context) -> {
            ConfigManager.reloadStringConfig(value.getConfigFile(), value.getConfigPath(), false);
        });
    }

    public static double getDeltaTime() {
        return DeltaTimeTracker.getDeltaSeconds();
    }
}
