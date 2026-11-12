package it.hurts.octostudios.octolib.module.config.annotation.registration;

import it.hurts.octostudios.octolib.module.config.impl.OctoConfig;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface AnnotationConfigFactory<T extends Annotation> {
    
    OctoConfig create(T annotation, Object object);
    
}
