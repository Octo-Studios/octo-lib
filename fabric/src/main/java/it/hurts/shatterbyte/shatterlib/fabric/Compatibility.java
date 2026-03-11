package it.hurts.shatterbyte.shatterlib.fabric;

import it.hurts.shatterbyte.shatterlib.ShatterLib;

public class Compatibility {
    public static void init() {
        if (ShatterLib.IRIS_LOADED) {
            IrisCompat.registerPipelines();
        }
    }
}
