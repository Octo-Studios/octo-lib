package it.hurts.octostudios.octolib.modules.config.loader;

import dev.architectury.platform.Platform;
import it.hurts.octostudios.octolib.modules.config.cfgbuilder.CompoundEntry;
import it.hurts.octostudios.octolib.modules.config.cfgbuilder.ConfigEntry;
import it.hurts.octostudios.octolib.modules.config.provider.ConfigProvider;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SolidConfigLoader<T> implements IConfigFileLoader<T, T> {
    
    @Override
    public void saveToFiles(String filePath, T data, ConfigProvider provider) {
        var file = Platform.getConfigFolder().resolve(filePath + ".yaml").toFile();
        
        if (file.getParentFile().isDirectory() || file.getParentFile().mkdirs()) {
            try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                provider.save(writer, data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public T loadFiles(String filePath, ConfigEntry pattern, ConfigProvider provider) {
        var file = Platform.getConfigFolder().resolve(filePath + ".yaml").toFile();
        if (file.isFile()) {
            try (var reader = YamlConfigReader.open(file.toPath())) {
                return (T) provider.load(reader, (CompoundEntry) pattern);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    
}
