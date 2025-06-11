package it.hurts.octostudios.octolib.module.config.annotation.registration;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface ConfigNameGetter<T extends Annotation> {
    
    String getName(T annotation, Object object);
    
}
