package it.hurts.shatterbyte.byteapi.module.config;

import de.marhali.json5.Json5Object;
import it.hurts.shatterbyte.byteapi.ByteAPI;
import it.hurts.shatterbyte.byteapi.module.config.network.SyncServerConfigPacket;
import it.hurts.shatterbyte.byteapi.module.network.ByteAPINetwork;
import it.hurts.shatterbyte.byteapi.platform.ByteAPIServices;
import lombok.SneakyThrows;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class ConfigManager {
    private static final Map<Class<? extends ShatterConfig>, Map<Integer, SchemaFixer>> SCHEMA_FIXERS = new HashMap<>();

    public static final Map<String, List<ShatterConfig>> CONFIGS_BY_MODID = new LinkedHashMap<>();

    private static final Map<String, ShatterConfig> CLIENT = new HashMap<>();
    private static final Map<String, ShatterConfig> COMMON = new HashMap<>();
    private static final Map<String, ShatterConfig> SERVER = new HashMap<>();

    public static final String SERVER_CONFIG = "serverconfig";

    @SneakyThrows
    public static void register(String modId, ShatterConfig config) {
        CONFIGS_BY_MODID.putIfAbsent(modId, new ArrayList<>());

        switch (config.getSide()) {
            case CLIENT -> {
                if (!ByteAPIServices.platform().isClientEnvironment()) {
                    break;
                }

                CLIENT.put(config.getPath(), config);
            }
            case COMMON -> COMMON.put(config.getPath(), config);
            case SERVER -> SERVER.put(config.getPath(), config);
        }

        CONFIGS_BY_MODID.get(modId).add(config);
        SCHEMA_FIXERS.put(config.getClass(), new HashMap<>());
    }

    public static Collection<ShatterConfig> getCommonConfigs() {
        return COMMON.values();
    }

    public static Collection<ShatterConfig> getServerConfigs() {
        return SERVER.values();
    }

    public static Collection<ShatterConfig> getClientConfigs() {
        return CLIENT.values();
    }

    public static List<ShatterConfig> getConfigsForMod(String modId) {
        return CONFIGS_BY_MODID.getOrDefault(modId, Collections.emptyList());
    }

    public static @Nullable ShatterConfig getFirstForMod(String modId) {
        List<ShatterConfig> configs = getConfigsForMod(modId);
        if (configs.isEmpty()) {
            return null;
        }

        return configs.getFirst();
    }

    public static void loadAllCommonConfigs() {
        ConfigManager.getCommonConfigs().forEach(config -> config.load(ByteAPIServices.platform().getConfigDirectory()));
    }

    public static void loadAllClientConfigs() {
        ConfigManager.getClientConfigs().forEach(config -> config.load(ByteAPIServices.platform().getConfigDirectory()));
    }

    public static void loadAllServerConfigs() {
        ConfigManager.getServerConfigs().forEach(config -> config.load(ByteAPIServices.platform().getConfigDirectory()));
    }

    public static void loadServerConfigOverrides(Path serverConfigFolder) {
        Path commonFolder = ByteAPIServices.platform().getConfigDirectory();

        for (ShatterConfig config : ConfigManager.getServerConfigs()) {
            config.load(commonFolder);

            Path overrideFile = serverConfigFolder.resolve(config.getFileName());
            if (Files.exists(overrideFile)) {
                config.load(serverConfigFolder, false);
            }
        }
    }

    public static void syncServerConfig(MinecraftServer server, String path) {
        ShatterConfig config = ConfigManager.getConfig(path);
        if (config == null) {
            ByteAPI.LOGGER.error("Couldn't sync config with all players: {}, config not found.", path);
            return;
        }

        ByteAPINetwork.sendToPlayers(server.getPlayerList().getPlayers(), new SyncServerConfigPacket(config));
    }

    public static void syncServerConfig(ServerPlayer player, String path) {
        ShatterConfig config = ConfigManager.getConfig(path);
        if (config == null) {
            ByteAPI.LOGGER.error("Couldn't sync config: {}, config not found.", path);
            return;
        }

        ByteAPINetwork.sendToPlayer(player, new SyncServerConfigPacket(config));
    }

    public static void syncServerConfigs(ServerPlayer serverPlayer) {
        for (ShatterConfig serverConfig : ConfigManager.getServerConfigs()) {
            ByteAPINetwork.sendToPlayer(serverPlayer, new SyncServerConfigPacket(serverConfig));
        }
    }

    public static Set<String> getCommonPaths() {
        return COMMON.keySet();
    }

    public static Set<String> getServerPaths() {
        return SERVER.keySet();
    }

    public static Set<String> getCommonAndServerPaths() {
        Set<String> set = new HashSet<>();

        set.addAll(COMMON.keySet());
        set.addAll(SERVER.keySet());

        return set;
    }

    public static Set<String> getClientPaths() {
        return CLIENT.keySet();
    }

    public static ShatterConfig getConfig(String path) {
        if (COMMON.containsKey(path)) {
            return COMMON.get(path);
        }

        if (SERVER.containsKey(path)) {
            return SERVER.get(path);
        }

        if (!ByteAPIServices.platform().isClientEnvironment()) {
            return null;
        }

        if (CLIENT.containsKey(path)) {
            return CLIENT.get(path);
        }

        return null;
    }

    public static boolean reload(String path, @Nullable MinecraftServer server) {
        if (SERVER.containsKey(path)) {
            ShatterConfig serverConfig = SERVER.get(path);
            serverConfig.load(ByteAPIServices.platform().getConfigDirectory());

            if (server != null) {
                Path serverConfigFolder = getServerConfigFolder(server);
                Path overrideFile = serverConfigFolder.resolve(serverConfig.getFileName());
                if (Files.exists(overrideFile)) {
                    serverConfig.load(serverConfigFolder, false);
                }

                ConfigManager.syncServerConfig(server, path);
            }

            return true;
        }

        if (COMMON.containsKey(path)) {
            COMMON.get(path).load(ByteAPIServices.platform().getConfigDirectory());
            return true;
        }

        if (!ByteAPIServices.platform().isClientEnvironment()) {
            return false;
        }

        if (CLIENT.containsKey(path)) {
            CLIENT.get(path).load(ByteAPIServices.platform().getConfigDirectory());
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

    public static Path getServerConfigFolder(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve(SERVER_CONFIG);
    }
}
