package it.hurts.octostudios.octolib.quilt;

import it.hurts.octostudios.octolib.OctoLibClient;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public final class OctoLibQuiltClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        OctoLibClient.init();
    }
}