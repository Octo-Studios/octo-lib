package it.hurts.sskirillss.octolib.config.annotations.registration;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface ConfigNameGetter<T extends Annotation> {
    
    String getName(T annotation, Object object);
    
}
