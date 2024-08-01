package it.hurts.sskirillss.octolib.config.loader;

import it.hurts.sskirillss.octolib.config.cfgbuilder.ArrayEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.ConfigEntry;
import it.hurts.sskirillss.octolib.config.provider.ConfigProvider;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class FileSpreadLoader<T> implements IConfigFileLoader<Iterable<T>, List<T>> {
    
    @Override
    public void saveToFiles(String filePath, Iterable<T> data, ConfigProvider provider) {
        var file = PATH.resolve(filePath + ".yaml").toFile();
    
        if (file.getParentFile().isDirectory() || file.getParentFile().mkdirs()) {
            try (FileWriter writer = new FileWriter(file)) {
                provider.saveAll(writer, data.iterator());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public List<T> loadFiles(String filePath, ConfigEntry pattern, ConfigProvider provider) {
        var file = PATH.resolve(filePath + ".yaml").toFile();
    
        if (file.isFile()) {
            try (FileReader writer = new FileReader(file)) {
                return provider.loadAll(writer, (Iterator) ((ArrayEntry) pattern).iterator());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    
}
