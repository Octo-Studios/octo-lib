package it.hurts.shatterbyte.shatterlib.event;

import it.hurts.shatterbyte.shatterlib.client.particle.ParticleSystem;
import it.hurts.shatterbyte.shatterlib.util.CommonCode;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class LevelRenderEvents {
    
    @SubscribeEvent
    public static void renderLevelRender(RenderLevelStageEvent.AfterParticles event) {
        CommonCode.renderTrails(event.getLevelRenderState().cameraRenderState.pos, Minecraft.getInstance().renderBuffers().bufferSource(), event.getPoseStack(), Minecraft.getInstance().getDeltaTracker());
    }

    @SubscribeEvent
    public static void renderScreenParticles(ScreenEvent.Render.Post e) {
        ParticleSystem.renderScreenParticles(e.getScreen(), e.getGuiGraphics(), Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
    }
}
