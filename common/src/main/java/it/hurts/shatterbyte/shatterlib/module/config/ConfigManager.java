package it.hurts.shatterbyte.shatterlib.module.config;

import com.mojang.datafixers.util.Pair;
import dev.architectury.networking.NetworkManager;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.registration.AnnotationConfigFactory;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.registration.Config;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.registration.ConfigNameGetter;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.registration.ObjectConfig;
import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.CompoundEntry;
import it.hurts.shatterbyte.shatterlib.module.config.impl.*;
import it.hurts.shatterbyte.shatterlib.module.config.impl.ShatterConfigBase;
import it.hurts.shatterbyte.shatterlib.module.config.impl.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.network.SyncConfigPacket;
import it.hurts.shatterbyte.shatterlib.module.config.provider.ConfigProvider;
import it.hurts.shatterbyte.shatterlib.module.config.provider.ConfigProviderBase;
import it.hurts.shatterbyte.shatterlib.module.config.util.ConfigUtils;
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
    private static final Map<String, ShatterConfig> CONFIG_MAP = new ConcurrentHashMap<>();
    private static final Map<String, ConfigProvider> CUSTOM_CONFIG_PROVIDERS = new HashMap<>();
    private static final IdentityHashMap<Class<? extends Annotation>, Pair<AnnotationConfigFactory<?>, ConfigNameGetter<?>>> ANNOTATION_CONFIG_FACTORIES = new IdentityHashMap<>();
    
    static {
        BASE_PROVIDER = ConfigProviderBase.getDefault(4);
        registerConfigFactory(Config.class,
                (a, object) -> {
                    String name = a.value();
                    if (!ShatterConfig.class.isAssignableFrom(object.getClass()))
                        throw new ClassCastException(String.format("Config object (%s) must implement ShatterConfig", name));
                    
                    return (ShatterConfig) object;
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
                        case SOLID_OBJECT -> new ShatterConfigBase(object, side);
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
    
    public static ShatterConfig getConfig(String location) {
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
    
    public static void registerConfig(String location, ShatterConfig config) {
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
    
    private static synchronized void reload(String location, ShatterConfig config, boolean saveToFile) {
        var provider = getConfigProvider(location);
        
        Object object = config.prepareData();
        
        try {
            var pattern = provider.createPattern(object);
            var data = config.getLoader().loadFiles(location, pattern, provider);
            provider.insert2ndStep(object, data);
            config.onLoadObject(object);
        } catch (Exception e) {
            ShatterLib.LOGGER.error("Error occurs while reading " + location + " config.");
            throw new RuntimeException(e);
        } finally {
            if (saveToFile)
                config.getLoader().saveToFiles(location, Cast.cast(object), provider);
        }
    }
    
    public static synchronized void reloadStringConfig(String stringData, String location, boolean saveToFile) {
        var provider = getConfigProvider(location);
        var config = getConfig(location);
        
        Object object = config.prepareData();
        StringReader reader = new StringReader(stringData);
        
        try {
            var pattern = provider.createPattern(object);
            var data = provider.load(reader, (CompoundEntry) pattern);
            provider.insert2ndStep(object, data);
            config.onLoadObject(object);
        } catch (Exception e) {
            ShatterLib.LOGGER.error("Error occurs while reading " + location + " config.");
            throw new RuntimeException(e);
        } finally {
            if (saveToFile)
                config.getLoader().saveToFiles(location, Cast.cast(object), provider);
        }
    }
    
    public static synchronized String saveAsString(String location) {
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
    
    private static void uploadDataToConfig(String location, ShatterConfig config) {
        var provider = getConfigProvider(location);
        Object object = config.prepareData();
        config.getLoader().saveToFiles(location, Cast.cast(object), provider);
    }
    
    public static void reload(String location) {
        var config = CONFIG_MAP.get(location);
        reload(location, config);
    }
    
    public static void reload(String location, ShatterConfig config) {
        reload(location, config, true);
    }
    
}
