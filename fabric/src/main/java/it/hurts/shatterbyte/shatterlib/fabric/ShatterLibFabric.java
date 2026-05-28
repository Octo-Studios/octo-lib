package it.hurts.shatterbyte.shatterlib.fabric;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.platform.ShatterLibServices;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class ShatterLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ShatterLibServices.initialize(FabricPlatformHelper.INSTANCE);
        ShatterLib.init();
        ShatterLib.onCommonSetup();
        CommandRegistrationCallback.EVENT.register(ShatterLib::registerCommands);
        ServerLifecycleEvents.SERVER_STARTING.register(ShatterLib::onServerBeforeStart);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ShatterLib.onPlayerJoin(handler.player));
    }
}
