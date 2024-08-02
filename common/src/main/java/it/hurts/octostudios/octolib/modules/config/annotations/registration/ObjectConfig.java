package it.hurts.octostudios.octolib.modules.config.annotations.registration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectConfig {
    
    String value();
    
    ConfigType type() default ConfigType.SOLID_OBJECT;
    
    enum ConfigType {
        FILE_SPREAD,
        SOLID_OBJECT
    }
    
}
