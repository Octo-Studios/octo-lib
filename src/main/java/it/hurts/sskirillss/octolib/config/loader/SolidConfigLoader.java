package it.hurts.sskirillss.octolib.config.loader;

import it.hurts.sskirillss.octolib.config.cfgbuilder.ArrayEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.CompoundEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.ConfigEntry;
import it.hurts.sskirillss.octolib.config.provider.ConfigProvider;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class SolidConfigLoader<T> implements IConfigFileLoader<T, T> {
    
    @Override
    public void saveToFiles(String filePath, T data, ConfigProvider provider) {
        var file = PATH.resolve(filePath + ".yaml").toFile();
        
        if (file.getParentFile().isDirectory() || file.getParentFile().mkdirs()) {
            try (FileWriter writer = new FileWriter(file)) {
                provider.save(writer, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public T loadFiles(String filePath, ConfigEntry pattern, ConfigProvider provider) {
        var file = PATH.resolve(filePath + ".yaml").toFile();
        if (file.isFile()) {
            try (FileReader writer = new FileReader(file)) {
                return (T) provider.load(writer, (CompoundEntry) pattern);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    
}
