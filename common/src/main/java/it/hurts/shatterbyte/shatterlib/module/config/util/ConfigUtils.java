package it.hurts.shatterbyte.shatterlib.module.config.util;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Cast;

import java.lang.reflect.Field;

@UtilityClass
public class ConfigUtils {
    
    public static void registerFieldConfig(Field field, String dir) {
        field.setAccessible(true);
        try {
            var value = field.get(null);
            
            if (value == null)
                ShatterLib.LOGGER.warn("Failed to load config in " + dir + " directory: config is null.");
            
            for (var a : field.getAnnotations()) {
                var fabric = ConfigManager.getConfigFactory(a.annotationType());
                
                if (fabric == null) {
                    ShatterLib.LOGGER.warn("Unsupported annotation {} for config initialization.", a);
                    continue;
                }
                
                var shatterConfig = fabric.getFirst().create(Cast.cast(a), value);
                var name = fabric.getSecond().getName(Cast.cast(a), value);
                
                String location = dir + "/" + name;
                ConfigManager.registerConfig(location, shatterConfig);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
}
