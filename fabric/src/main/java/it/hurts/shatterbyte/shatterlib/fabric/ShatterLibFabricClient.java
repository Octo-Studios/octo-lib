package it.hurts.shatterbyte.shatterlib.fabric;

import it.hurts.shatterbyte.shatterlib.ShatterLibClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public final class ShatterLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ShatterLibClient.init();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> ShatterLibFabricClientCommands.register(dispatcher));
        ClientTickEvents.START_WORLD_TICK.register(ShatterLibClient::onClientLevelPre);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ShatterLibClient.onClientDisconnect());
    }
}
