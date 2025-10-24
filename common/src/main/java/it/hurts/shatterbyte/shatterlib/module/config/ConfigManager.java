package it.hurts.shatterbyte.shatterlib.module.config;

import lombok.SneakyThrows;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigManager {
    private static Map<String, ShatterConfig> COMMON = new HashMap<>();
    //private static Map<String, ShatterConfig> SERVER = new HashMap<>();

    @SneakyThrows
    public static void registerCommon(ShatterConfig config) {
        COMMON.put(config.getPath(), config);
    }

    public static Collection<ShatterConfig> getCommonConfigs() {
        return COMMON.values();
    }

    public static Set<String> getCommonPaths() {
        return COMMON.keySet();
    }
}
