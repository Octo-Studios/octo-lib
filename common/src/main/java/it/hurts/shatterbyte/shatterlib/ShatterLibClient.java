package it.hurts.shatterbyte.shatterlib;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import it.hurts.shatterbyte.shatterlib.client.animation.TweenSystem;
import it.hurts.shatterbyte.shatterlib.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.shatterlib.util.DeltaTimeTracker;

public final class ShatterLibClient {
    public static void init() {
        registerEvents();

        TweenSystem.init();
        //EntityTrailRegistry.registerProvider(EntityType.ARROW, TestArrowTrail::new);
    }
    
    private static void registerEvents() {
        ClientTickEvent.CLIENT_LEVEL_PRE.register(ShatterRenderManager::clientTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(ShatterRenderManager::worldExit);
    }

    public static double getDeltaTime() {
        return DeltaTimeTracker.getDeltaSeconds();
    }
}
