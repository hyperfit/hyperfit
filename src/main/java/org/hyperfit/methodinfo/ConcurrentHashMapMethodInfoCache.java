package org.hyperfit.methodinfo;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches method metadata
 *
 * @author Carlos Perez
 */
public class ConcurrentHashMapMethodInfoCache implements MethodInfoCache {

    /*ConcurrentHashMap doesn't block the entire map for every thread,
    so it scales better for more threads, meaning it's faster*/
    private final Map<Method, MethodInfo> methodInfoCache = new ConcurrentHashMap<Method, MethodInfo>();

    /**
     * since the block is not synchronized, multiple threads can add the same key. They can pass the null check,
     * one adds the key and returns its value, and then the other one will override that value with a new one
     * and returns it. However that won't corrupt the data.
     */
    public MethodInfo get(Method method) {
        if (method == null) throw new IllegalArgumentException("method must not be null");

        MethodInfo methodInfo = methodInfoCache.get(method);
        if (methodInfo == null) { //remember that local vars are thread safe
            methodInfo = new MethodInfo(method);
            methodInfoCache.put(method, methodInfo);
        }

        return methodInfo;
    }

    /**
     * @return the previous value associated to method
     */
    public MethodInfo put(Method method, MethodInfo methodInfo) {
        return methodInfoCache.put(method, methodInfo);
    }
}
