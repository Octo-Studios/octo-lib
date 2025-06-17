package it.hurts.octostudios.octolib;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import it.hurts.octostudios.octolib.module.particle.OctoRenderManager;
import it.hurts.octostudios.octolib.module.particle.trail.EntityTrailRegistry;
import it.hurts.octostudios.octolib.module.particle.trail.TestArrowTrail;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;

public final class OctoLibClient {
    public static void init() {
        registerEvents();
        EntityTrailRegistry.registerProvider(EntityType.ARROW, TestArrowTrail::new);
    }
    
    private static void registerEvents() {
        ClientTickEvent.CLIENT_LEVEL_PRE.register(OctoRenderManager::clientTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(OctoRenderManager::worldExit);
    }

    public static double getDeltaTime() {
        return Minecraft.getInstance().getFrameTimeNs() / 1000000000d;
    }
}
