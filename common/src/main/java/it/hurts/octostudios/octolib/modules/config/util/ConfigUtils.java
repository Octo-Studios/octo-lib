package it.hurts.octostudios.octolib.modules.config.util;

import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import it.hurts.octostudios.octolib.modules.utils.Cast;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class ConfigUtils {
    
    public static void registerFieldConfig(Field field, String dir) {
        field.setAccessible(true);
        try {
            var value = field.get(null);
            
            if (value == null)
                OctoLib.LOGGER.warn("Failed to load config in " + dir + " directory: config is null.");
            
            for (var a : field.getAnnotations()) {
                var fabric = ConfigManager.getConfigFactory(a.annotationType());
                
                if (fabric == null) {
                    OctoLib.LOGGER.warn("Unsupported annotation {} for config initialization.", a);
                    continue;
                }
                
                var octoConfig = fabric.getFirst().create(Cast.cast(a), value);
                var name = fabric.getSecond().getName(Cast.cast(a), value);
                
                String location = dir + "/" + name;
                ConfigManager.registerConfig(location, octoConfig);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
}
