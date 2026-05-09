package it.hurts.shatterbyte.byteapi.fabric;

import it.hurts.shatterbyte.byteapi.ByteAPI;
import it.hurts.shatterbyte.byteapi.platform.ByteAPIServices;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class ByteAPIFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ByteAPIServices.initialize(FabricPlatformHelper.INSTANCE);
        ByteAPI.init();
        ByteAPI.onCommonSetup();
        CommandRegistrationCallback.EVENT.register(ByteAPI::registerCommands);
        ServerLifecycleEvents.SERVER_STARTING.register(ByteAPI::onServerBeforeStart);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ByteAPI.onPlayerJoin(handler.player));
    }
}
