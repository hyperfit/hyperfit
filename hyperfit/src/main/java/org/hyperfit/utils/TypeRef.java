package org.hyperfit.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * See <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html">Super type token</a>
 *
 * References a generic type.
 *
 * @author crazybob@google.com (Bob Lee)
 */

/*
The abstract qualifier is intentional. It forces clients to subclass
 this in order to create a new instance of TypeReference. You make a super type token for List<String> like this:
TypeRef<List<String>> x = new TypeRef<List<String>>() {};
*/
public abstract class TypeRef<T> {

    private final Type type;
    private Class<T> clazz;

    protected TypeRef() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];

        this.clazz = type instanceof Class<?>
                //NOTE: i'm really confused about this being ok or not..i like that it gives me types
                ? (Class<T>) type
                : (Class<T>) ((ParameterizedType) type).getRawType();
    }

    /**
     * Gets the referenced type.
     */
    final public Type getType() {
        return this.type;
    }

    final public Class<T> getClazz() {
        return clazz;
    }

}