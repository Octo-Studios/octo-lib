package it.hurts.sskirillss.octolib.config;

import it.hurts.sskirillss.octolib.config.provider.ConfigProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public final class ConfigManager {
    
    private static final HashMap<ResourceLocation, OctoConfig> CONFIG_MAP = new HashMap<>();
    private static final HashMap<ResourceLocation, ConfigProvider> CUSTOM_CONFIG_PROVIDERS = new HashMap<>();
    public static final ConfigProvider BASE_PROVIDER;
    
    private static ConfigProvider getConfigProvider(ResourceLocation resourceLocation) {
        return CUSTOM_CONFIG_PROVIDERS.getOrDefault(resourceLocation, BASE_PROVIDER);
    }
    
    public static void registerConfigProvider(ResourceLocation location, ConfigProvider provider) {
        CUSTOM_CONFIG_PROVIDERS.put(location, provider);
    }
    
    static {
        BASE_PROVIDER = null;
    }

}
