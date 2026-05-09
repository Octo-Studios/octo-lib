package it.hurts.shatterbyte.byteapi.fabric;

import it.hurts.shatterbyte.byteapi.ByteAPIClient;
import it.hurts.shatterbyte.byteapi.util.CommonCode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;

public final class ByteAPIFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ByteAPIClient.init();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> ByteAPIFabricClientCommands.register(dispatcher));
        ClientTickEvents.START_LEVEL_TICK.register(ByteAPIClient::onClientLevelPre);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ByteAPIClient.onClientDisconnect());
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register((context -> {
            CommonCode.renderTrails(context.levelState().cameraRenderState.pos, context.bufferSource(), context.poseStack(), Minecraft.getInstance().getDeltaTracker());
        }));
    }
}
