package it.hurts.octostudios.octolib.modules.config.provider;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.CompoundEntry;
import it.hurts.octostudios.octolib.modules.config.cfgbuilder.ConfigEntry;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

public interface ConfigProvider {
    
    ConfigEntry createPattern(Object object);
    
    void save(Writer writer, Object config);
    
    void saveAll(Writer writer, Iterator<?> config);
    
    Object load(Reader reader, CompoundEntry pattern);
    
    <T> List<T> loadAll(Reader reader, Iterator<CompoundEntry> patternIterator);
    
    <T> T insert2ndStep(T target, T data);
    
}