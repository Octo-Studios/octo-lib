package it.hurts.octostudios.octolib.modules.config.annotations;

import org.yaml.snakeyaml.introspector.BeanAccess;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TypeProp {
    String inlineComment() default "";
    
    String comment() default "";
    
    BeanAccess accessType() default BeanAccess.FIELD;
    
    boolean onlyProps() default false;
}
