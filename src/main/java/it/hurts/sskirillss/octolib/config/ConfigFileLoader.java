package it.hurts.sskirillss.octolib.config;

import it.hurts.sskirillss.octolib.config.provider.ConfigProvider;
import net.neoforged.fml.loading.FMLConfig;

public class ConfigFileLoader implements IConfigFileLoader{
    
    @Override
    public void saveToFiles(String filePath, FileSpreadType spreadType, Object object, ConfigProvider provider) {
//        switch (spreadType) {
//            case FILE_SPREAD ->
//        }
    }
    
    @Override
    public Object loadFiles(String filePath, FileSpreadType spreadType, ConfigProvider provider) {
        return null;
    }
    
    
    
}
