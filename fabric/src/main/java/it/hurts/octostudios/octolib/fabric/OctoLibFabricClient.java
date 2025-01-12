package it.hurts.octostudios.octolib.fabric;

import it.hurts.octostudios.octolib.OctoLibClient;
import net.fabricmc.api.ClientModInitializer;

public final class OctoLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OctoLibClient.init();
    }
}