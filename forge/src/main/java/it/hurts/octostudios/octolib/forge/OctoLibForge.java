package it.hurts.octostudios.octolib.forge;

import it.hurts.octostudios.octolib.OctoLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(OctoLib.MODID)
public final class OctoLibForge {
    public OctoLibForge() {
        OctoLib.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
            new OctoLibForgeClient();
    }
}
