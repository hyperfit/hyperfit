package org.hyperfit.annotation;

import org.hyperfit.http.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for use on resource methods that denotes the result is determined by the identified link
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Link {

    String value();  //rel

    String name() default "";

    Method methodType() default Method.GET;


}