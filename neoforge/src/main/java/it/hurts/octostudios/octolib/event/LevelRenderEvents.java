package it.hurts.octostudios.octolib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.octostudios.octolib.module.particle.OctoRenderManager;
import it.hurts.octostudios.octolib.module.particle.RenderProvider;
import it.hurts.octostudios.octolib.util.CommonCode;
import it.hurts.octostudios.octolib.util.TesselatorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class LevelRenderEvents {
    
    @SubscribeEvent
    public static void renderLevelRender(RenderLevelStageEvent.AfterParticles event) {
        CommonCode.renderTrails(event.getCamera(), Minecraft.getInstance().renderBuffers().bufferSource(), event.getPoseStack(), event.getPartialTick());
    }
}
