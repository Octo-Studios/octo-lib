package it.hurts.shatterbyte.shatterlib.module.config.annotation.registration;

import it.hurts.shatterbyte.shatterlib.module.config.impl.ShatterConfig;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface AnnotationConfigFactory<T extends Annotation> {
    
    ShatterConfig create(T annotation, Object object);
    
}
