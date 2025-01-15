package it.hurts.octostudios.octolib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import it.hurts.octostudios.octolib.modules.particles.RenderProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OctoLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class LevelRenderEvents {

	@SubscribeEvent
	public static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			return;
		}

		Vec3 cameraPos = event.getCamera().getPosition();
		double camX = cameraPos.x();
		double camY = cameraPos.y();
		double camZ = cameraPos.z();

		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		PoseStack poseStack = event.getPoseStack();
		float partialTicks = event.getPartialTick();

		for (RenderProvider provider : OctoRenderManager.getProviders()) {
			Vec3 renderPos = provider.getRenderPosition(partialTicks);
			poseStack.pushPose();
			poseStack.translate(renderPos.x - camX, renderPos.y - camY, renderPos.z - camZ);
			provider.render(partialTicks, poseStack, bufferSource);
			poseStack.popPose();
		}
		bufferSource.endBatch();
	}
}
