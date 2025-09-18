package it.hurts.shatterbyte.shatterlib.fabric;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import net.fabricmc.api.ModInitializer;

public final class ShatterLibFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ShatterLib.init();
    }
    
}
