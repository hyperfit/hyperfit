package org.hyperfit.annotation;

import org.hyperfit.http.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Annotation for method that denotes the result is determined by following a link relationship
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Link {

    String value();  //rel

    String name() default "";

    Method methodType() default Method.GET;

    // LINK PARAMETERS    
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Param {

        String value();
    }

    // LINK HEADERS
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Header {

        String value();
    }

    // LINK CONTENT
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Content {

        String type();
    }
}