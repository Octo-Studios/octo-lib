package it.hurts.sskirillss.octolib.config.storage;

import it.hurts.sskirillss.octolib.config.data.OctoConfig;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigStorage {
    private static final Map<Path, OctoConfig> CONFIGS = new HashMap<>();

    public static void set(OctoConfig config) {
        CONFIGS.put(config.getConstructor().getPath(), config);
    }

    public static OctoConfig get(Path path) {
        return CONFIGS.get(path);
    }
}