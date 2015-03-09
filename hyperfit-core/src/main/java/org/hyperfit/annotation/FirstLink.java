package org.hyperfit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotation for use on resource methods that denotes the result is determined by first matching link
 * For all links with the given relationship, finds the first entry in the given name list that matches the name of a link
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FirstLink {

    public static final String NULL = "Java will not let you use null for attribute values so use this instead and let us hope no one ever names a link this";
    public static final String MATCH_ANY_NAME = "This should match every link name";

    /**
     * @return the link relationship used to limit the search area
     */
    String rel();

    /**
     * @return The ordered list of names to check for matches.  Use NULL to match links with no name, use MATCH_ANY_NAME to match a link with any name.
     */
    String[] names() default {};

}