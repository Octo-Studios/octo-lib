package it.hurts.octostudios.octolib.fabric;

import net.fabricmc.api.ModInitializer;

import it.hurts.octostudios.octolib.OctoLib;

public final class OctoLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OctoLib.init();
    }
}
