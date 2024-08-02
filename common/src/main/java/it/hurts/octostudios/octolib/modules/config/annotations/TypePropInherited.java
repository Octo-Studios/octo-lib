package it.hurts.octostudios.octolib.modules.config.annotations;

import org.yaml.snakeyaml.introspector.BeanAccess;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface TypePropInherited {
    String inlineComment() default "";
    
    String comment() default "";
    
    BeanAccess accessType() default BeanAccess.FIELD;
    
    boolean onlyProps() default false;
}
