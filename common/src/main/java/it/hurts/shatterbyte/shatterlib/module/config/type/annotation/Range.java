package it.hurts.shatterbyte.shatterlib.module.config.type.annotation;

import java.lang.annotation.*;

/**
 * Adds a range hint to the field it's applied to
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {
    double min() default Double.NEGATIVE_INFINITY;
    double max() default Double.POSITIVE_INFINITY;
}
