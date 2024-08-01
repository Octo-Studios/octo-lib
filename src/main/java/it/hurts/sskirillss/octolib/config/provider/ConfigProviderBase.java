package it.hurts.sskirillss.octolib.config.provider;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CompoundEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.ConfigEntry;
import it.hurts.sskirillss.octolib.config.util.*;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

@Getter
public class ConfigProviderBase implements ConfigProvider {
    
    private final Yaml yamlConverted;
    private final ConstructorExt constructor;
    private final RepresenterExt patternRepresenter;
    private final RepresenterExt configRepresenter;
    private final DumperOptions options;
    private final ConfigEntryConverter configEntryConverter;
    private final EntryInjector<ConfigEntry> injector;
    
    public ConfigProviderBase(ConstructorExt constructor, RepresenterExt patternRepresenter, RepresenterExt configRepresenter, DumperOptions options) {
        this(constructor, patternRepresenter, configRepresenter, options, new SchemeInjector());
    }
    
    public ConfigProviderBase(ConstructorExt constructor, RepresenterExt patternRepresenter, RepresenterExt configRepresenter, DumperOptions options, EntryInjector<ConfigEntry> injector) {
        this.constructor = constructor;
        this.patternRepresenter = patternRepresenter;
        this.configRepresenter = configRepresenter;
        this.options = options;
        this.injector = injector;
        this.yamlConverted = new Yaml(constructor, configRepresenter, options);
        this.configEntryConverter = new ConfigEntryConverter(constructor, patternRepresenter);
    }
    
    public static ConfigProviderBase getDefault(int indent) {
        PropertyUtils propertyUtils = new PropertyUtilsExt();
        
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setProcessComments(true);
        loaderOptions.setTagInspector(tag -> true);
        var constructor = new ConstructorExt(loaderOptions);
        constructor.setPropertyUtils(propertyUtils);
    
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setProcessComments(true);
        options.setIndent(indent);
    
        var configRepresenter = new RepresenterWithoutTag(options);
        configRepresenter.setPropertyUtils(propertyUtils);
    
        RepresenterExt patternRepresenter = new RepresenterExt(options);
        patternRepresenter.setPropertyUtils(new PropertyUtilsExt());
        
        return new ConfigProviderBase(constructor, patternRepresenter, configRepresenter, options);
    }
    
    @Override
    public ConfigEntry createPattern(Object object) {
        return configEntryConverter.representDeconstructed(object);
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
    public Object load(FileReader reader, CompoundEntry pattern) {
        var compound = yamlConverted.loadAs(reader, CompoundEntry.class);
        if (compound == null)
            return null;
        var resultFirst = injector.apply(pattern, compound);
        return configEntryConverter.construct(resultFirst);
    }
    
    @Override
    public <T> List<T> loadAll(FileReader reader, Iterator<CompoundEntry> patternIterator) {
        Iterable<Object> objects = yamlConverted.loadAll(reader);
        ArrayList<T> list = new ArrayList<>();
        for (var object : objects) {
            var pattern = patternIterator.next();
            var compound = configEntryConverter.represent(object);
            var resultFirst = injector.apply(pattern, compound);
            list.add(configEntryConverter.construct(resultFirst));
        }
        return list;
    }
    
    @Override
    public <T> T insert2ndStep(T target, T data) {
        var props = patternRepresenter.getPropertyUtils().getProperties(target.getClass());
        
        if (Collection.class.isAssignableFrom(target.getClass())) {
            rewriteCollection((Collection<?>) target, (Collection<?>) data);
            return target;
        }
    
        if (Map.class.isAssignableFrom(target.getClass())) {
            rewriteMap((Map<?, ?>) target, (Map<?, ?>) data);
            return target;
        }
        
        for (var prop : props) {
            try {
                if (Set.class.isAssignableFrom(prop.getClass()) || List.class.isAssignableFrom(prop.getClass()))
                    rewriteCollection((Set<?>) prop.get(target), (Collection<?>) prop.get(data));
                else if (Map.class.isAssignableFrom(prop.getClass()))
                    rewriteMap((Map<?, ?>) prop.get(target), (Map<?, ?>) prop.get(data));
                else
                    prop.set(target, prop.get(data));
            } catch (Exception ignored) {
            }
        }
        return target;
    }
    
    protected void rewriteCollection(Collection<?> target, Collection<?> data) {
        target.clear();
        target.addAll((Collection) data);
    }
    
    protected void rewriteMap(Map<?, ?> target, Map<?, ?> data) {
        target.clear();
        target.putAll((Map) data);
    }
    
}
