package it.hurts.sskirillss.octolib.config.provider;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CompoundEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.ConfigEntry;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

public interface ConfigProvider {
    
    ConfigEntry createPattern(Object object);
    
    void save(FileWriter writer, Object config);
    
    void saveAll(FileWriter writer, Iterator<?> config);
    
    Object load(FileReader reader, CompoundEntry pattern);
    
    <T> List<T> loadAll(FileReader reader, Iterator<CompoundEntry> patternIterator);
    
    <T> T insert2ndStep(T target, T data);
    
}