package it.hurts.sskirillss.octolib.config;

import com.mojang.datafixers.util.Pair;
import it.hurts.sskirillss.octolib.OctoLib;
import it.hurts.sskirillss.octolib.config.annotations.registration.AnnotationConfigFabric;
import it.hurts.sskirillss.octolib.config.annotations.registration.ConfigNameGetter;
import it.hurts.sskirillss.octolib.config.annotations.registration.Config;
import it.hurts.sskirillss.octolib.config.annotations.registration.ObjectConfig;
import it.hurts.sskirillss.octolib.config.impl.*;
import it.hurts.sskirillss.octolib.config.provider.ConfigProvider;
import it.hurts.sskirillss.octolib.config.provider.ConfigProviderBase;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.util.Cast;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class ConfigManager {
    
    private static final HashMap<ResourceLocation, OctoConfig> CONFIG_MAP = new HashMap<>();
    private static final HashMap<ResourceLocation, ConfigProvider> CUSTOM_CONFIG_PROVIDERS = new HashMap<>();
    private static final IdentityHashMap<Class<? extends Annotation>, Pair<AnnotationConfigFabric<?>, ConfigNameGetter<?>>> ANNOTATION_CONFIG_FABRICS = new IdentityHashMap<>();
    public static final ConfigProvider BASE_PROVIDER;
    
    private static ConfigProvider getConfigProvider(ResourceLocation resourceLocation) {
        return CUSTOM_CONFIG_PROVIDERS.getOrDefault(resourceLocation, BASE_PROVIDER);
    }
    
    public static OctoConfig getConfig(ResourceLocation resourceLocation) {
        return CONFIG_MAP.get(resourceLocation);
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
    
    public static void registerConfigProvider(ResourceLocation location, ConfigProvider provider) {
        CUSTOM_CONFIG_PROVIDERS.put(location, provider);
    }
    
    public static void registerConfig(ResourceLocation location, OctoConfig config) {
        CONFIG_MAP.put(location, config);
    }
    
    public static void reloadAll() {
        CONFIG_MAP.forEach(ConfigManager::reload);
    }
    
    private static void reload(ResourceLocation location, OctoConfig config) {
        var provider = getConfigProvider(location);
    
        String filePath = location.getPath();
        Object object = config.prepareData();
        var pattern = provider.createPattern(object);
    
        try {
            var data = config.getLoader().loadFiles(filePath, pattern, provider);
            provider.insert2ndStep(object, data);
            config.onLoadObject(object);
        } catch (Exception e) {
            OctoLib.LOGGER.error("Error occurs while " + location.getNamespace() + " config reload.");
        } finally {
            config.getLoader().saveToFiles(filePath, Cast.cast(object), provider);
        }
    }
    
    public static void reload(ResourceLocation location) {
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
                            
                            yield  new FileSpreadConfig((Collection<?>) object);
                        }
                        case SOLID_OBJECT -> new OctoConfigBase(object);
                    };
                },
                (a, object) -> a.value());
    }

}
