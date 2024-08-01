package it.hurts.sskirillss.octolib.config.loader;

import it.hurts.sskirillss.octolib.config.cfgbuilder.ConfigEntry;
import it.hurts.sskirillss.octolib.config.provider.ConfigProvider;

import java.util.HashMap;
import java.util.Map;

public class DirectorySpreadLoader<T> implements IConfigFileLoader<Map<String, T>, Map<String, T>> {
    
    IConfigFileLoader<T, T> fileLoader;
    
    public DirectorySpreadLoader(IConfigFileLoader<T, T> fileLoader) {
        this.fileLoader = fileLoader;
    }
    
    @Override
    public void saveToFiles(String filePath, Map<String, T> data, ConfigProvider provider) {
        var iterator = data.entrySet().iterator();
        
        if (iterator.hasNext()) {
            var next = iterator.next();
            fileLoader.saveToFiles(filePath + next.getKey(), next.getValue(), provider);
        }
    }
    
    @Override
    public Map<String, T> loadFiles(String filePath, ConfigEntry pattern, ConfigProvider provider) {
        Map<String, T> map = new HashMap<>();
        var dir = PATH.resolve(filePath).toFile();
        
        if (!dir.isDirectory() || dir.listFiles() == null)
            return null;
        
        for (var file : dir.listFiles()) {
            map.put(file.getName(), fileLoader.loadFiles(filePath, pattern, provider));
        }
        return map;
    }
    
}
