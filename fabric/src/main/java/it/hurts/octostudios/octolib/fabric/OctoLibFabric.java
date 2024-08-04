package it.hurts.octostudios.octolib.fabric;

import it.hurts.octostudios.octolib.OctoLib;
import net.fabricmc.api.ModInitializer;

public final class OctoLibFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        OctoLib.init();
    }
    
}
