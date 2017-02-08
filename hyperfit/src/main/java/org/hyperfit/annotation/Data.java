package org.hyperfit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for extracting data by providing a path within the resource
 * An attempt to convert the destination of the path into the return type of the
 * annotated method is made.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Data {

    /**
     *
     * @return The path to follow to reach the destination
     */
    String[] value();

}
