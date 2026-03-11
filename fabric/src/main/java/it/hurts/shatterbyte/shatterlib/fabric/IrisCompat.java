package it.hurts.shatterbyte.shatterlib.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisProgram;

import static it.hurts.shatterbyte.shatterlib.util.TesselatorUtils.TRAIL_PIPELINE;

public class IrisCompat {
    static {
        if(!FabricLoader.getInstance().isModLoaded("iris"))
        {
            throw new RuntimeException("No Iris Shaders found, ignoring...");
        }
    }

    public static void registerPipelines() {
        IrisApi.getInstance().assignPipeline(TRAIL_PIPELINE, IrisProgram.ENTITIES);
    }
}
