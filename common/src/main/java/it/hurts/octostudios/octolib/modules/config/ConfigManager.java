package it.hurts.octostudios.octolib.modules.config;

import com.mojang.datafixers.util.Pair;
import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.modules.config.annotations.registration.AnnotationConfigFabric;
import it.hurts.octostudios.octolib.modules.config.annotations.registration.Config;
import it.hurts.octostudios.octolib.modules.config.annotations.registration.ConfigNameGetter;
import it.hurts.octostudios.octolib.modules.config.annotations.registration.ObjectConfig;
import it.hurts.octostudios.octolib.modules.config.impl.*;
import it.hurts.octostudios.octolib.modules.config.provider.ConfigProvider;
import it.hurts.octostudios.octolib.modules.config.provider.ConfigProviderBase;
import org.apache.logging.log4j.util.Cast;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {
    
    private static final Map<String, OctoConfig> CONFIG_MAP = new ConcurrentHashMap<>();
    private static final HashMap<String, ConfigProvider> CUSTOM_CONFIG_PROVIDERS = new HashMap<>();
    private static final IdentityHashMap<Class<? extends Annotation>, Pair<AnnotationConfigFabric<?>, ConfigNameGetter<?>>> ANNOTATION_CONFIG_FABRICS = new IdentityHashMap<>();
    public static final ConfigProvider BASE_PROVIDER;
    
    private static ConfigProvider getConfigProvider(String location) {
        return CUSTOM_CONFIG_PROVIDERS.getOrDefault(location, BASE_PROVIDER);
    }
    
    public static OctoConfig getConfig(String location) {
        return CONFIG_MAP.get(location);
    }
    
    @Nullable
    public static Pair<AnnotationConfigFabric<?>, ConfigNameGetter<?>> getConfigFabric(Class<? extends Annotation> clazz) {
        return ANNOTATION_CONFIG_FABRICS.get(clazz);
    }
    
    public static <T extends Annotation> void registerConfigFabric(Class<? extends T> annotation,
                                                                   AnnotationConfigFabric<T> fabric,
                                                                   ConfigNameGetter<T> nameGetter) {
        ANNOTATION_CONFIG_FABRICS.put(annotation, Pair.of(fabric,  nameGetter));
    }
    
    public static void registerConfigProvider(String location, ConfigProvider provider) {
        CUSTOM_CONFIG_PROVIDERS.put(location, provider);
    }
    
    public static void registerConfig(String location, OctoConfig config) {
        CONFIG_MAP.put(location, config);
    }
    
    public static void reloadAll() {
        CONFIG_MAP.forEach(ConfigManager::reload);
    }
    
    private static void reload(String location, OctoConfig config) {
        var provider = getConfigProvider(location);
        
        Object object = config.prepareData();
    
        try {
            var pattern = provider.createPattern(object);
            var data = config.getLoader().loadFiles(location, pattern, provider);
            provider.insert2ndStep(object, data);
            config.onLoadObject(object);
        } catch (Exception e) {
            OctoLib.LOGGER.error("Error occurs while " + location + " config reload.");
            e.printStackTrace();
        } finally {
            config.getLoader().saveToFiles(location, Cast.cast(object), provider);
        }
    }
    
    public static void reload(String location) {
        var config = CONFIG_MAP.get(location);
        reload(location, config);
    }
    
    static {
        BASE_PROVIDER = ConfigProviderBase.getDefault(4);
        registerConfigFabric(Config.class,
                (a, object) -> {
                    String name = a.value();
                    if (!OctoConfig.class.isAssignableFrom(object.getClass()))
                        throw new ClassCastException(String.format("Config object (%s) must implement OctoConfig", name));
    
                    return (OctoConfig) object;
                },
                (a, object) -> a.value());
        registerConfigFabric(ObjectConfig.class,
                (a, object) -> {
                    String name = a.value();
                    return switch (a.type()) {
                        case FILE_SPREAD -> {
                            if (!Collection.class.isAssignableFrom(object.getClass()))
                                throw new ClassCastException(String.format("Config object (%s) must be a collection", name));
                            
                            yield new FileSpreadConfig((Collection<?>) object);
                        }
                        case SOLID_OBJECT -> new OctoConfigBase(object);
                    };
                },
                (a, object) -> a.value());
    }

}
