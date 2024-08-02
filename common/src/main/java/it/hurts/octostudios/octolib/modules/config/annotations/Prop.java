package it.hurts.octostudios.octolib.modules.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Prop {
    
    String name() default "";
    
    String comment() default "";
    
    String inlineComment() default "";
    
}
