package org.hyperfit.methodinfo;

import java.lang.reflect.Method;

/**
 * Caches method metadata
 *
 * @author Carlos Perez
 */
public interface MethodInfoCache {

    /**
     * @return the MethodInfo object associated with method
     */
    MethodInfo get(Method method);

    /**
     * @return the previous value associated with method
     */
    MethodInfo put(Method method, MethodInfo methodInfo);
}
