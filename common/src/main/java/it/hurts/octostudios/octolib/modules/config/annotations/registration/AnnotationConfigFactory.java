package it.hurts.octostudios.octolib.modules.config.annotations.registration;

import it.hurts.octostudios.octolib.modules.config.impl.OctoConfig;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface AnnotationConfigFactory<T extends Annotation> {
    
    OctoConfig create(T annotation, Object object);
    
}
