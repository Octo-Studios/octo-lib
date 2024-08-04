package it.hurts.octostudios.octolib;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PlatformImpl implements IPlatform {
    
    @Override
    public Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir();
    }
    
}
