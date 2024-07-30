package it.hurts.sskirillss.octolib.config;

import it.hurts.sskirillss.octolib.config.provider.ConfigProvider;


public interface IConfigFileLoader {
    
    void saveToFiles(String filePath, FileSpreadType spreadType, Object object, ConfigProvider provider);
    
    Object loadFiles(String filePath, FileSpreadType spreadType, ConfigProvider provider);
    
}




