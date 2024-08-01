package it.hurts.sskirillss.octolib;

import it.hurts.sskirillss.octolib.config.annotations.registration.Config;
import it.hurts.sskirillss.octolib.config.annotations.registration.ConfigRegistration;
import it.hurts.sskirillss.octolib.config.cfgbuilder.CompoundEntry;
import it.hurts.sskirillss.octolib.config.impl.CompoundConfig;

import java.util.HashMap;
import java.util.Map;

@ConfigRegistration(modId = OctoLib.MODID)
public class ConfigInit {
    public static final Map<Integer, TestConfig> CONFIGS = new HashMap<>();

    @Config("test")
    public static CompoundConfig CONFIG = new CompoundConfig() {
        @Override
        public void write(CompoundEntry compoundEntry) {
            for (int i = 0; i < 10; i++) {
                compoundEntry.putObject("entry_" + i, new TestConfig());
            }
        }

        @Override
        public void read(CompoundEntry compoundEntry) {
            for (int i = 0; i < 10; i++) {
                CONFIGS.put(i, compoundEntry.getObject("entry_" + i, TestConfig.class));
            }
        }

        @Override
        protected boolean spreadFiles() {
            return true;
        }
    };
}