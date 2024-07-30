package it.hurts.sskirillss.octolib.config.provider;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CompoundEntry;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

public interface ConfigProvider {
    
    CompoundEntry createPattern(Object object);
    
    void save(FileWriter writer, Object config);
    
    void saveAll(FileWriter writer, Iterator<?> config);
    
    Object load(FileReader reader, Class<?> classType, CompoundEntry pattern);
    
    <T> List<T> loadAll(FileReader reader, Class<T> classType, Iterator<CompoundEntry> patternIterator);
    
}