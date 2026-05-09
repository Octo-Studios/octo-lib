package it.hurts.shatterbyte.byteapi.module.config.type.annotation;

import java.lang.annotation.*;

/**
 * Adds a range hint to the field it's applied to
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {
    double min();
    double max();
    double step();
}
