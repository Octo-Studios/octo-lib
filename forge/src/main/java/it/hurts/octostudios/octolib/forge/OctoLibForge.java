package it.hurts.octostudios.octolib.forge;

import it.hurts.octostudios.octolib.OctoLib;
import net.minecraftforge.fml.common.Mod;

@Mod(OctoLib.MODID)
public final class OctoLibForge {
    public OctoLibForge() {
        OctoLib.init();
    }
}
