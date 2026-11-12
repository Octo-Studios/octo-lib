package it.hurts.octostudios.octolib.event;

import it.hurts.octostudios.octolib.client.particle.ParticleSystem;
import it.hurts.octostudios.octolib.util.CommonCode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class LevelRenderEvents {
    
    @SubscribeEvent
    public static void renderLevelRender(RenderLevelStageEvent.AfterParticles event) {
        CommonCode.renderTrails(event.getCamera(), Minecraft.getInstance().renderBuffers().bufferSource(), event.getPoseStack(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void renderScreenParticles(ScreenEvent.Render.Post e) {
        ParticleSystem.renderScreenParticles(e.getScreen(), e.getGuiGraphics(), Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
    }
}
