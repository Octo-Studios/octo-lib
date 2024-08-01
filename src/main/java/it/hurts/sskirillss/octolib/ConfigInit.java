package it.hurts.sskirillss.octolib;

import it.hurts.sskirillss.octolib.config.ConfigManager;
import it.hurts.sskirillss.octolib.config.annotations.registration.ConfigRegistration;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@ConfigRegistration(modId = OctoLib.MODID)
public class ConfigInit {
    public static final Map<Integer, TestConfig> CONFIGS = new HashMap<>();

    static {
        for (int i = 0; i < 10; i++) {
            TestConfig config = new TestConfig();

            ConfigManager.registerConfig(ResourceLocation.fromNamespaceAndPath(OctoLib.MODID, "test/entry_" + i), config);

            CONFIGS.put(i, config);
        }
    }
}