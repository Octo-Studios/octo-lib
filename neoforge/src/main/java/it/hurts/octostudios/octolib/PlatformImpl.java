package it.hurts.octostudios.octolib;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PlatformImpl implements IPlatform {
    
    @Override
    public Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get();
    }
    
}
