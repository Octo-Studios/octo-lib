package it.hurts.octostudios.octolib;

import org.quiltmc.loader.api.QuiltLoader;

import java.nio.file.Path;

public class PlatformImpl implements IPlatform {
    
    @Override
    public Path getConfigPath() {
        return QuiltLoader.getConfigDir();
    }
    
}
