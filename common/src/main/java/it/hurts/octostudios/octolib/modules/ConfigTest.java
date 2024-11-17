package it.hurts.octostudios.octolib.modules;

import it.hurts.octostudios.octolib.modules.config.annotations.registration.ObjectConfig;
import it.hurts.octostudios.octolib.modules.config.impl.ConfigSide;

public class ConfigTest {
    
    @ObjectConfig(value = "clientc", side = ConfigSide.CLIENT)
    static ConfigA configA = new ConfigA();
    
    @ObjectConfig(value = "serverc", side = ConfigSide.SERVER)
    static ConfigA configB = new ConfigA();
    
    public static class ConfigA {
        
        String a = "It's a config";
        int lol = 109;
        
    }
}
