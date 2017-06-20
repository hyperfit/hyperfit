package org.hyperfit.utils;


import org.hyperfit.exception.HyperfitException;

import java.lang.reflect.Array;

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
            throw new HyperfitException("Cannot cast object [" + value + "] to " + clazz, ex );
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

}
