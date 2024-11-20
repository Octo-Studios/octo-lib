package it.hurts.octostudios.octolib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import it.hurts.octostudios.octolib.modules.particles.RenderProvider;
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
    public static void renderLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
            return;
        
        Vec3 vec3 = event.getCamera().getPosition();
        double d = vec3.x();
        double e = vec3.y();
        double g = vec3.z();
        
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack poseStack = new PoseStack();
        float f = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        
        for (RenderProvider trail : OctoRenderManager.getProviders()) {
            var position = trail.getRenderPosition(f);
            poseStack.pushPose();
            poseStack.translate(position.x - d, position.y - e, position.z - g);
            trail.render(f, poseStack, bufferSource);
            poseStack.popPose();
        }
        bufferSource.endBatch();
    }
    
    
}
