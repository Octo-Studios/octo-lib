package it.hurts.octostudios.octolib;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import it.hurts.octostudios.octolib.client.animation.TweenSystem;
import it.hurts.octostudios.octolib.module.particle.OctoRenderManager;
import it.hurts.octostudios.octolib.module.post_effect.init.OctoLibPostEffects;
import it.hurts.octostudios.octolib.module.post_effect.instances.ChromaticAberrationPostEffect;

public final class OctoLibClient {
    public static long DELTA_NANOS;

    public static void init() {
        registerEvents();
        //EntityTrailRegistry.registerProvider(EntityType.ARROW, TestArrowTrail::new);

        OctoLibClient.registerPostEffects();

        TweenSystem.init();
    }

    private static void registerEvents() {
        ClientTickEvent.CLIENT_LEVEL_PRE.register(OctoRenderManager::clientTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(OctoRenderManager::worldExit);
    }

    private static void registerPostEffects() {
        OctoLibPostEffects.register(ChromaticAberrationPostEffect::new);

        OctoLibPostEffects.init();
    }

    public static double getDeltaTime() {
        return DELTA_NANOS / 1000000000d;
    }
}
