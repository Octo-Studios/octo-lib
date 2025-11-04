package it.hurts.shatterbyte.shatterlib.module.config;

import lombok.SneakyThrows;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class ConfigManager {
    private static final Map<String, ShatterConfig> COMMON = new HashMap<>();
    private static final Set<String> SERVER = new HashSet<>();

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

    public static Set<String> getServerPaths() {
        return SERVER;
    }

    public static void syncConfigs(ServerPlayer serverPlayer) {

    }
}
