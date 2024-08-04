package it.hurts.octostudios.octolib.quilt;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import it.hurts.octostudios.octolib.OctoLib;

public final class OctoLibQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        OctoLib.init();
    }
}
