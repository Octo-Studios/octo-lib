package it.hurts.shatterbyte.shatterlib.client.config;

import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClientConfigManager {
    private static Map<String, ShatterConfig> CLIENT = new HashMap<>();

    @SneakyThrows
    public static void register(ShatterConfig config) {
        CLIENT.put(config.getPath(), config);
    }

    public static Collection<ShatterConfig> getConfigs() {
        return CLIENT.values();
    }

    public static Set<String> getPaths() {
        return CLIENT.keySet();
    }
}
