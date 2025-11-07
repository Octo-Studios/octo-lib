package it.hurts.shatterbyte.shatterlib.module.config;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import lombok.SneakyThrows;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.compress.archivers.sevenz.CLI;

import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    private static final Map<String, ShatterConfig> CLIENT = new HashMap<>();
    private static final Map<String, ShatterConfig> COMMON = new HashMap<>();
    public static final LevelResource SERVER_CONFIG = new LevelResource("serverconfig");

    @SneakyThrows
    public static void register(ShatterConfig config) {
        switch (config.getSide()) {
            case CLIENT -> {
                if (Platform.getEnvironment() != Env.CLIENT) {
                    break;
                }

                CLIENT.put(config.getSuffixedName(), config);
            }
            case COMMON -> COMMON.put(config.getSuffixedName(), config);
            case SERVER -> ShatterLib.LOGGER.warn("idk man");
        }
    }

    public static Collection<ShatterConfig> getCommonConfigs() {
        return COMMON.values();
    }

    public static Collection<ShatterConfig> getClientConfigs() {
        return CLIENT.values();
    }

    public static void loadAllCommonConfigs() {
        ConfigManager.getCommonConfigs().forEach(config -> config.load(Platform.getConfigFolder()));
    }

    public static void loadAllClientConfigs() {
        ConfigManager.getClientConfigs().forEach(config -> config.load(Platform.getConfigFolder()));
    }

    public static void loadAllServerConfigs(Path serverConfigFolder) {

    }

    public static void syncConfigs(ServerPlayer serverPlayer) {

    }

    public static Set<String> getCommonPaths() {
        return COMMON.keySet();
    }

    public static Set<String> getClientPaths() {
        return CLIENT.keySet();
    }

    public static boolean reload(String path) {
        if (COMMON.containsKey(path)) {
            COMMON.get(path).load(Platform.getConfigFolder());
            return true;
        }

        if (Platform.getEnvironment() != Env.CLIENT) {
            return false;
        }

        if (CLIENT.containsKey(path)) {
            CLIENT.get(path).load(Platform.getConfigFolder());
            return true;
        }

        return false;
    }
}
