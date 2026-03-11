package it.hurts.shatterbyte.shatterlib.fabric;

import it.hurts.shatterbyte.shatterlib.ShatterLibClient;
import it.hurts.shatterbyte.shatterlib.util.CommonCode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleRenderEvents;
import net.minecraft.client.Minecraft;

public final class ShatterLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ShatterLibClient.init();
        Compatibility.init();

//        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
//            CommonCode.renderTrails(context.camera(), Minecraft.getInstance().renderBuffers().bufferSource(), context.matrixStack(), context.tickCounter());
//        });
    }
}