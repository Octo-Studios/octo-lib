package it.hurts.octostudios.octolib.modules.config.loader;

import it.hurts.octostudios.octolib.Services;
import it.hurts.octostudios.octolib.modules.config.cfgbuilder.CompoundEntry;
import it.hurts.octostudios.octolib.modules.config.cfgbuilder.ConfigEntry;
import it.hurts.octostudios.octolib.modules.config.provider.ConfigProvider;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SolidConfigLoader<T> implements IConfigFileLoader<T, T> {
    
    @Override
    public void saveToFiles(String filePath, T data, ConfigProvider provider) {
        var file = Services.PLATFORM.getConfigPath().resolve(filePath + ".yaml").toFile();
        
        if (file.getParentFile().isDirectory() || file.getParentFile().mkdirs()) {
            try (FileWriter writer = new FileWriter(file)) {
                provider.save(writer, data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public T loadFiles(String filePath, ConfigEntry pattern, ConfigProvider provider) {
        var file = Services.PLATFORM.getConfigPath().resolve(filePath + ".yaml").toFile();
        if (file.isFile()) {
            try (FileReader reader = new FileReader(file)) {
                return (T) provider.load(reader, (CompoundEntry) pattern);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    
}
