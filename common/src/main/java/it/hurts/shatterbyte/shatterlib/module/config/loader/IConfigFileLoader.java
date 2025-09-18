package it.hurts.shatterbyte.shatterlib.module.config.loader;

import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.ConfigEntry;
import it.hurts.shatterbyte.shatterlib.module.config.provider.ConfigProvider;

public interface IConfigFileLoader<U1, U2> {
    
    SolidConfigLoader<?> SOLID = new SolidConfigLoader<>();
    
    void saveToFiles(String filePath, U1 data, ConfigProvider provider);
    
    U2 loadFiles(String filePath, ConfigEntry pattern, ConfigProvider provider);
    
}




