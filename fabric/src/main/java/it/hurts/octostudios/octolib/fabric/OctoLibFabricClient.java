package it.hurts.octostudios.octolib.fabric;

import it.hurts.octostudios.octolib.OctoLibClient;
import it.hurts.octostudios.octolib.util.CommonCode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;

public final class OctoLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OctoLibClient.init();

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            CommonCode.renderTrails(context.camera(), Minecraft.getInstance().renderBuffers().bufferSource(), context.matrixStack(), context.tickCounter());
        });
    }
}