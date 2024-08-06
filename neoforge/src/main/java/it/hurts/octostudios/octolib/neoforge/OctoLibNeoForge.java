package it.hurts.octostudios.octolib.neoforge;

import net.neoforged.fml.common.Mod;

import it.hurts.octostudios.octolib.OctoLib;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(OctoLib.MODID)
public final class OctoLibNeoForge {
    public OctoLibNeoForge() {
        OctoLib.init();
    }
}
