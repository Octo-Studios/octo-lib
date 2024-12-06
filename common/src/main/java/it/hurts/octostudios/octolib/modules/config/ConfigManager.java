package it.hurts.octostudios.octolib.modules.config;

import com.mojang.datafixers.util.Pair;
import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.modules.config.annotations.registration.AnnotationConfigFactory;
import it.hurts.octostudios.octolib.modules.config.annotations.registration.Config;
import it.hurts.octostudios.octolib.modules.config.annotations.registration.ConfigNameGetter;
import it.hurts.octostudios.octolib.modules.config.annotations.registration.ObjectConfig;
import it.hurts.octostudios.octolib.modules.config.cfgbuilder.CompoundEntry;
import it.hurts.octostudios.octolib.modules.config.impl.ConfigSide;
import it.hurts.octostudios.octolib.modules.config.impl.FileSpreadConfig;
import it.hurts.octostudios.octolib.modules.config.impl.OctoConfig;
import it.hurts.octostudios.octolib.modules.config.impl.OctoConfigBase;
import it.hurts.octostudios.octolib.modules.config.network.SyncConfigPacket;
import it.hurts.octostudios.octolib.modules.config.provider.ConfigProvider;
import it.hurts.octostudios.octolib.modules.config.provider.ConfigProviderBase;
import it.hurts.octostudios.octolib.modules.config.util.ConfigUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.util.Cast;
import org.jetbrains.annotations.Nullable;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {
    
    public static final ConfigProvider BASE_PROVIDER;
    private static final HashSet<String> SERVER_CONFIGS = new HashSet<>();
    private static final Map<String, OctoConfig> CONFIG_MAP = new ConcurrentHashMap<>();
    private static final Map<String, ConfigProvider> CUSTOM_CONFIG_PROVIDERS = new HashMap<>();
    private static final IdentityHashMap<Class<? extends Annotation>, Pair<AnnotationConfigFactory<?>, ConfigNameGetter<?>>> ANNOTATION_CONFIG_FACTORIES = new IdentityHashMap<>();
    
    static {
        BASE_PROVIDER = ConfigProviderBase.getDefault(4);
        registerConfigFactory(Config.class,
                (a, object) -> {
                    String name = a.value();
                    if (!OctoConfig.class.isAssignableFrom(object.getClass()))
                        throw new ClassCastException(String.format("Config object (%s) must implement OctoConfig", name));
                    
                    return (OctoConfig) object;
                },
                (a, object) -> a.value());
        registerConfigFactory(ObjectConfig.class,
                (a, object) -> {
                    String name = a.value();
                    ConfigSide side = a.side();
                    return switch (a.type()) {
                        case FILE_SPREAD -> {
                            if (!Collection.class.isAssignableFrom(object.getClass()))
                                throw new ClassCastException(String.format("Config object (%s) must be a collection", name));
                            
                            yield new FileSpreadConfig((Collection<?>) object, side);
                        }
                        case SOLID_OBJECT -> new OctoConfigBase(object, side);
                    };
                },
                (a, object) -> a.value());
    }
    
    public static Set<String> getAllPaths() {
        return CONFIG_MAP.keySet();
    }
    
    private static ConfigProvider getConfigProvider(String location) {
        return CUSTOM_CONFIG_PROVIDERS.getOrDefault(location, BASE_PROVIDER);
    }
    
    public static OctoConfig getConfig(String location) {
        return CONFIG_MAP.get(location);
    }
    
    @Nullable
    public static Pair<AnnotationConfigFactory<?>, ConfigNameGetter<?>> getConfigFactory(Class<? extends Annotation> clazz) {
        return ANNOTATION_CONFIG_FACTORIES.get(clazz);
    }
    
    public static <T extends Annotation> void registerConfigFactory(Class<? extends T> annotation,
                                                                    AnnotationConfigFactory<T> fabric,
                                                                    ConfigNameGetter<T> nameGetter) {
        ANNOTATION_CONFIG_FACTORIES.put(annotation, Pair.of(fabric, nameGetter));
    }
    
    public static void registerConfigProvider(String location, ConfigProvider provider) {
        CUSTOM_CONFIG_PROVIDERS.put(location, provider);
    }
    
    public static void registerConfigPackage(Class<?> configPackage, String dir) {
        for (var field : configPackage.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()))
                ConfigUtils.registerFieldConfig(field, dir);
        }
    }
    
    public static Set<String> getServerConfigs() {
        return SERVER_CONFIGS;
    }
    
    public static boolean isServerConfig(String name) {
        return SERVER_CONFIGS.contains(name);
    }
    
    public static void registerConfig(String location, OctoConfig config) {
        CONFIG_MAP.put(location, config);
        
        try {
            ConfigManager.reload(location);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        
        if (config.getSide() == ConfigSide.SERVER)
            SERVER_CONFIGS.add(location);
    }
    
    public static void reloadAll() {
        CONFIG_MAP.forEach(ConfigManager::reload);
    }
    
    private static void reload(String location, OctoConfig config, boolean saveToFile) {
        var provider = getConfigProvider(location);
        
        Object object = config.prepareData();
        
        try {
            var pattern = provider.createPattern(object);
            var data = config.getLoader().loadFiles(location, pattern, provider);
            provider.insert2ndStep(object, data);
            config.onLoadObject(object);
        } catch (Exception e) {
            OctoLib.LOGGER.error("Error occurs while reading " + location + " config.");
            throw new RuntimeException(e);
        } finally {
            if (saveToFile)
                config.getLoader().saveToFiles(location, Cast.cast(object), provider);
        }
    }
    
    public static void reloadStringConfig(String stringData, String location, boolean saveToFile) {
        var provider = getConfigProvider(location);
        var config = getConfig(location);
        
        Object object = config.prepareData();
        System.out.println(stringData);
        StringReader reader = new StringReader(stringData);
        
        try {
            var pattern = provider.createPattern(object);
            var data = provider.load(reader, (CompoundEntry) pattern);
            provider.insert2ndStep(object, data);
            config.onLoadObject(object);
        } catch (Exception e) {
            OctoLib.LOGGER.error("Error occurs while reading " + location + " config.");
            throw new RuntimeException(e);
        } finally {
            if (saveToFile)
                config.getLoader().saveToFiles(location, Cast.cast(object), provider);
        }
    }
    
    public static String saveAsString(String location) {
        var provider = getConfigProvider(location);
        var config = getConfig(location);
        
        Object object = config.prepareData();
        
        StringWriter writer = new StringWriter();
        provider.save(writer, object);
        
        return writer.toString();
    }
    
    public static void syncConfig(String path, MinecraftServer server) {
        NetworkManager.sendToPlayers(server.getPlayerList().getPlayers(), new SyncConfigPacket(path));
    }
    
    public static void syncConfig(ServerPlayer player, String path) {
        NetworkManager.sendToPlayer(player, new SyncConfigPacket(path));
    }
    
    public static void syncConfigs(ServerPlayer player) {
        for (var path : SERVER_CONFIGS)
            syncConfig(player, path);
    }
    
    public static void uploadDataToConfig(String location) {
        var config = CONFIG_MAP.get(location);
        uploadDataToConfig(location, config);
    }
    
    private static void uploadDataToConfig(String location, OctoConfig config) {
        var provider = getConfigProvider(location);
        Object object = config.prepareData();
        config.getLoader().saveToFiles(location, Cast.cast(object), provider);
    }
    
    public static void reload(String location) {
        var config = CONFIG_MAP.get(location);
        reload(location, config);
    }
    
    public static void reload(String location, OctoConfig config) {
        reload(location, config, true);
    }
    
}
