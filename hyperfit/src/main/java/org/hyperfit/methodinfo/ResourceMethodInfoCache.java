package org.hyperfit.methodinfo;

/**
 * ResourceMethodInfoCache is a cache for the method metadata in a class
 *
 * @author Carlos Perez
 */
public interface ResourceMethodInfoCache {

    /**
     * @return the MethodInfoCache object associated with clazz
     */
    MethodInfoCache get(Class<?> clazz);

    /**
     * @return the previous value associated with method
     */
    MethodInfoCache put(Class<?> clazz, MethodInfoCache methodInfoCache);

}
