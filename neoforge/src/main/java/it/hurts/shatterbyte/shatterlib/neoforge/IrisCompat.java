package it.hurts.shatterbyte.shatterlib.neoforge;

import net.neoforged.fml.ModList;

import static it.hurts.shatterbyte.shatterlib.util.TesselatorUtils.TRAIL_PIPELINE;

public class IrisCompat {
    static {
        if(!ModList.get().isLoaded("iris"))
        {
            throw new RuntimeException("No Iris Shaders found, ignoring...");
        }
    }

    public static void registerPipelines() {
        //IrisApi.getInstance().assignPipeline(TRAIL_PIPELINE, IrisProgram.ENTITIES_TRANSLUCENT);
    }
}
