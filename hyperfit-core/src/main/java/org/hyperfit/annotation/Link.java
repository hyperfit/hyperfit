package org.hyperfit.annotation;

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

    public static final String NULL = "Java will not let you use null for attribute values so use this instead and let us hope no one ever names a link this";
    public static final String MATCH_ANY_NAME = "This should match every link name";

    String value();  //rel

    //exactly 1 link by rel, regardless of name         Link(rel)
    //exactly 1 link by rel & name                      NamedLink(rel,name)
    //exactly 1 link by rel & name (empty)              NamedLink(rel,""
    //exactly 1 link by rel & no name (null)            NamedLink(rel,NULL
    //first link by rel & name match                    FirstLink(rel,{name1,name2,NULL,MATCH}
    //first link by rel & name match                    FirstLink(rel,{name1}
    //all links of a given rel, regardless of name      Link(rel)
    //all links of a given rel & name                   NamedLink(rel,name)
    //

}