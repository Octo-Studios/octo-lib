package it.hurts.octostudios.octolib.modules.config.annotations.registration;

import it.hurts.octostudios.octolib.modules.config.impl.ConfigSide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    
    String value();
    
}
