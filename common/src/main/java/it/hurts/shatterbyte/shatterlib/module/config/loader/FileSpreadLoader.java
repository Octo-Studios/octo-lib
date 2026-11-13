package it.hurts.shatterbyte.shatterlib.module.config.loader;

import dev.architectury.platform.Platform;
import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.ArrayEntry;
import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.ConfigEntry;
import it.hurts.shatterbyte.shatterlib.module.config.provider.ConfigProvider;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

public class FileSpreadLoader<T> implements IConfigFileLoader<Iterable<T>, List<T>> {
    
    @Override
    public void saveToFiles(String filePath, Iterable<T> data, ConfigProvider provider) {
        var file = Platform.getConfigFolder().resolve(filePath + ".yaml").toFile();
    
        if (file.getParentFile().isDirectory() || file.getParentFile().mkdirs()) {
            try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                provider.saveAll(writer, data.iterator());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public List<T> loadFiles(String filePath, ConfigEntry pattern, ConfigProvider provider) {
        var file = Platform.getConfigFolder().resolve(filePath + ".yaml").toFile();
    
        if (file.isFile()) {
            try (var reader = YamlConfigReader.open(file.toPath())) {
                return provider.loadAll(reader, (Iterator) ((ArrayEntry) pattern).iterator());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    
}
