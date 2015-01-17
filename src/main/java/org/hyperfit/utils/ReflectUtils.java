package org.hyperfit.utils;


import org.hyperfit.exception.HyperClientException;
import org.hyperfit.message.Messages;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <p>Utility class for reflection operations.</p>
 *
 */
public class ReflectUtils {

    private ReflectUtils() {

    }

    /**
     * Casts value into class.
     * @param clazz class in which value will be casted
     * @param value value to cast
     * @return instance of class casted
     */
    public static <T> T cast(Class<T> clazz, Object value) {
        try {
            return clazz.cast(value);
        } catch (Exception ex) {
            throw new HyperClientException(ex, Messages.MSG_ERROR_REFLECTION_CANNOT_CAST, value, clazz);
        }
    }

    /**
     * Creates an empty array. 
     * @param type type of the array
     * @param size size of the array
     * @return array with the specified type.
     */
    public static Object[] createArray(Class<?> type, int size) {
        return cast(Object[].class, Array.newInstance(type, size));
    }

    /**
     * Gets type representing actual type of parameterized type.
     * @param type parameterized type
     * @param index index for the array of actual parameters
     * @return actual type
     */
    public static Type getArgumentType(ParameterizedType type, int index) {
        return type.getActualTypeArguments()[index];
    }

    /**
     * Returns raw class of specified type.
     * @param type
     * @return raw class.
     */
    public static Class getArgumentRawClass(Type type) {
        if (Class.class.isInstance(type)) {
            return cast(Class.class, type);
        } else {
            return cast(Class.class, cast(ParameterizedType.class, type).getRawType());
        }
    }

}
