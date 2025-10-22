package it.hurts.shatterbyte.shatterlib.module.config;

import it.hurts.shatterbyte.shatterlib.module.config.type.AbstractEntry;
import lombok.SneakyThrows;

import java.lang.management.ManagementPermission;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigManager {
    private static Map<String, ShatterConfig> REGISTRY = new HashMap<>();
    private static Map<String, ShatterConfig> DEFAULT_INSTANCES = new HashMap<>();

    @SneakyThrows
    public static void register(ShatterConfig config) {
        REGISTRY.put(config.getPath(), config);
        DEFAULT_INSTANCES.put(config.getPath(), config.getClass().getConstructor().newInstance());
    }

    public static Collection<ShatterConfig> getRegisteredConfigs() {
        return REGISTRY.values();
    }

    public static Set<String> getRegisteredPaths() {
        return REGISTRY.keySet();
    }

    public static ShatterConfig getDefaultInstance(ShatterConfig config) {
        return getDefaultInstance(config.getPath());
    }

    public static ShatterConfig getDefaultInstance(String path) {
        return DEFAULT_INSTANCES.get(path);
    }
}
