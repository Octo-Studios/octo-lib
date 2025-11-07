package it.hurts.shatterbyte.shatterlib.module.config;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import lombok.SneakyThrows;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

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

    public static void loadAllCommonConfigs() {
        ConfigManager.getCommonConfigs().forEach(config -> config.load(Platform.getConfigFolder()));
    }

    public static void loadAllClientConfigs() {
        ConfigManager.getClientConfigs().forEach(config -> config.load(Platform.getConfigFolder()));
    }

    public static void loadAllServerConfigs(Path serverConfigFolder) {

    }

    public static Set<String> getCommonPaths() {
        return COMMON.keySet();
    }

    public static void syncConfigs(ServerPlayer serverPlayer) {

    }
}
