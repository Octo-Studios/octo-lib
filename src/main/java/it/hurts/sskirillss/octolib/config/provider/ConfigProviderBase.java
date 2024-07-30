package it.hurts.sskirillss.octolib.config.provider;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CompoundEntry;
import it.hurts.sskirillss.octolib.config.util.*;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
public class ConfigProviderBase implements ConfigProvider {
    
    private final Yaml yamlConverted;
    private final ConstructorExt constructor;
    private final RepresenterExt patternRepresenter;
    private final RepresenterExt configRepresenter;
    private final DumperOptions options;
    private final CompoundConverter compoundConverter;
    private final EntryInjector<CompoundEntry> injector;
    
    public ConfigProviderBase(ConstructorExt constructor, RepresenterExt patternRepresenter, RepresenterExt configRepresenter, DumperOptions options) {
        this(constructor, patternRepresenter, configRepresenter, options, new SchemeInjector());
    }
    
    public ConfigProviderBase(ConstructorExt constructor, RepresenterExt patternRepresenter, RepresenterExt configRepresenter, DumperOptions options, EntryInjector<CompoundEntry> injector) {
        this.constructor = constructor;
        this.patternRepresenter = patternRepresenter;
        this.configRepresenter = configRepresenter;
        this.options = options;
        this.injector = injector;
        this.yamlConverted = new Yaml(constructor, configRepresenter, options);
        this.compoundConverter = new CompoundConverter(constructor, patternRepresenter);
    }
    
    @Override
    public CompoundEntry createPattern(Object object) {
        return compoundConverter.representDeconstructed(object);
    }
    
    @Override
    public void save(FileWriter writer, Object config) {
        yamlConverted.dump(config, writer);
    }
    
    @Override
    public void saveAll(FileWriter writer, Iterator<?> config) {
        yamlConverted.dumpAll(config, writer);
    }
    
    @Override
    public Object load(FileReader reader, Class<?> classType, CompoundEntry pattern) {
        var compound = yamlConverted.loadAs(reader, CompoundEntry.class);
        if (compound == null)
            return null;
        var resultFirst = injector.apply(pattern, compound);
        return compoundConverter.constructAs(resultFirst, classType);
    }
    
    @Override
    public <T> List<T> loadAll(FileReader reader, Class<T> classType, Iterator<CompoundEntry> patternIterator) {
        Iterable<Object> objects = yamlConverted.loadAll(reader);
        ArrayList<T> list = new ArrayList<>();
        for (var object : objects) {
            var pattern = patternIterator.next();
            var compound = compoundConverter.represent(object);
            var resultFirst = injector.apply(pattern, compound);
            list.add(compoundConverter.constructAs(resultFirst, classType));
        }
        return list;
    }
    
}
