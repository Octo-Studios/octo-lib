package it.hurts.shatterbyte.shatterlib.module.config;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import lombok.SneakyThrows;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class ConfigManager {
    private static final Map<String, ShatterConfig> CLIENT = new HashMap<>();
    private static final Map<String, ShatterConfig> COMMON = new HashMap<>();

    @SneakyThrows
    public static void register(ShatterConfig config) {
        switch (config.getSide()) {
            case CLIENT -> {
                if (Platform.getEnvironment() != Env.CLIENT) {
                    break;
                }

                CLIENT.put(config.getPath(), config);
            }
            case COMMON -> COMMON.put(config.getPath(), config);
            case SERVER -> ShatterLib.LOGGER.warn("idk man");
        }
    }

    public static Collection<ShatterConfig> getCommonConfigs() {
        return COMMON.values();
    }

    public static Collection<ShatterConfig> getClientConfigs() {
        return CLIENT.values();
    }

    public static Set<String> getCommonPaths() {
        return COMMON.keySet();
    }

    public static void syncConfigs(ServerPlayer serverPlayer) {

    }
}
