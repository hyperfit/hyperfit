package org.hyperfit.methodinfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ResourceMethodInfoCache is a cache for the method metadata in a class
 *
 * @author Carlos Perez
 */
public class ConcurrentHashMapResourceMethodInfoCache implements ResourceMethodInfoCache {

    /*ConcurrentHashMap doesn't block the entire map for every thread,
    so it scales better for more threads, meaning it's faster*/
    private final Map<Class<?>, MethodInfoCache> resourceMethodInfoMap = new ConcurrentHashMap<Class<?>, MethodInfoCache>();

    /**
     * since the block is not synchronized, multiple threads can add the same key. They can pass the null check,
     * one adds the key and returns its value, and then the other one will override that value with a new one
     * and returns it. However that won't corrupt the data.
     */
    public MethodInfoCache get(Class<?> clazz) {
        if (clazz == null) throw new IllegalArgumentException("class must not be null");

        MethodInfoCache methodInfoCache = resourceMethodInfoMap.get(clazz);
        if (methodInfoCache == null) {
            methodInfoCache = new ConcurrentHashMapMethodInfoCache();
            resourceMethodInfoMap.put(clazz, methodInfoCache);
        }
        return methodInfoCache;
    }

    /**
     * @return the previous value associated to clazz
     */
    public MethodInfoCache put(Class<?> clazz, MethodInfoCache methodInfoCache) {
        return resourceMethodInfoMap.put(clazz, methodInfoCache);
    }
}
