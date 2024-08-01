package it.hurts.sskirillss.octolib.config.loader;

import it.hurts.sskirillss.octolib.config.cfgbuilder.ConfigEntry;
import it.hurts.sskirillss.octolib.config.provider.ConfigProvider;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;


public interface IConfigFileLoader<U1, U2> {
    
    SolidConfigLoader<?> SOLID = new SolidConfigLoader<>();
    
    Path PATH = FMLPaths.CONFIGDIR.get();
    
    void saveToFiles(String filePath, U1 data, ConfigProvider provider);
    
    U2 loadFiles(String filePath, ConfigEntry pattern, ConfigProvider provider);
    
}




