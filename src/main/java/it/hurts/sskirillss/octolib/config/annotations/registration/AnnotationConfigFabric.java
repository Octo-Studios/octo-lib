package it.hurts.sskirillss.octolib.config.annotations.registration;

import it.hurts.sskirillss.octolib.config.impl.OctoConfig;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface AnnotationConfigFabric<T extends Annotation> {
    
    OctoConfig create(T annotation, Object object);
    
}
