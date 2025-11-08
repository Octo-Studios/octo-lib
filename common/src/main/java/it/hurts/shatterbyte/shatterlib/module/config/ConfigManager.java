package it.hurts.shatterbyte.shatterlib.module.config;

import de.marhali.json5.Json5Object;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import lombok.SneakyThrows;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class ConfigManager {
    private static final Map<Class<? extends ShatterConfig>, Map<Integer, SchemaFixer>> SCHEMA_FIXERS = new HashMap<>();

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

        SCHEMA_FIXERS.put(config.getClass(), new HashMap<>());
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

    public static void registerSchemaFixer(Class<? extends ShatterConfig> configClass, int from, int to, Consumer<Json5Object> fixer) {
        SCHEMA_FIXERS.get(configClass).put(from, new SchemaFixer(from, to, fixer));
    }

    public static SchemaFixer getFixer(Class<? extends ShatterConfig> configClass, int from) {
        return SCHEMA_FIXERS.get(configClass).get(from);
    }
}
