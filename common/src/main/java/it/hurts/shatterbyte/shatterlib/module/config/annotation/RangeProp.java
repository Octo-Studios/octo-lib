package it.hurts.shatterbyte.shatterlib.module.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RangeProp {
    String comment() default "";
    String inlineComment() default "";
    String stringFormat() default "%.2f";
    double min() default Double.NEGATIVE_INFINITY;
    double max() default Double.POSITIVE_INFINITY;
    boolean clamp() default false;
}