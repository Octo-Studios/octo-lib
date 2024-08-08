package it.hurts.octostudios.octolib.modules.config.loader;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.ConfigEntry;
import it.hurts.octostudios.octolib.modules.config.provider.ConfigProvider;

public interface IConfigFileLoader<U1, U2> {
    
    SolidConfigLoader<?> SOLID = new SolidConfigLoader<>();
    
    void saveToFiles(String filePath, U1 data, ConfigProvider provider);
    
    U2 loadFiles(String filePath, ConfigEntry pattern, ConfigProvider provider);
    
}




