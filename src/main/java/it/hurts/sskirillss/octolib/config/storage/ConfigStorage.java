package it.hurts.sskirillss.octolib.config.storage;

import it.hurts.sskirillss.octolib.config.api.IOctoConfig;
import it.hurts.sskirillss.octolib.config.data.OctoConfig;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigStorage {
    public static final Map<Path, OctoConfig> CONFIGS = new HashMap<>();

    public static void set(OctoConfig config) {
        CONFIGS.put(config.getConstructor().getPath(), config);
    }

    @Nullable
    public static OctoConfig get(IOctoConfig schema) {
        return CONFIGS.get(schema.getPath());
    }

    public static OctoConfig getOrSetup(IOctoConfig constructor) {
        OctoConfig config = get(constructor);

        if (config == null)
            constructor.setup();

        return config.getOrSetup();
    }
}