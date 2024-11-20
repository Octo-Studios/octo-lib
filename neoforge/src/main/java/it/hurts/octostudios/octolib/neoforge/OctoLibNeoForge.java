package it.hurts.octostudios.octolib.neoforge;

import it.hurts.octostudios.octolib.OctoLib;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(OctoLib.MODID)
public final class OctoLibNeoForge {
    public OctoLibNeoForge(IEventBus modBus) {
        OctoLib.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
            new OctoLibNeoForgeClient(modBus);
    }
}
