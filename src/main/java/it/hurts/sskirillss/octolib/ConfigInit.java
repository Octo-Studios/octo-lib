package it.hurts.sskirillss.octolib;

import it.hurts.sskirillss.octolib.config.annotations.registration.ConfigRegistration;
import it.hurts.sskirillss.octolib.config.annotations.registration.ObjectConfig;

@ConfigRegistration(modId = OctoLib.MODID)
public class ConfigInit {
    @ObjectConfig("octo-test")
    public static final TestConfig TEST_CONFIG = new TestConfig();
}