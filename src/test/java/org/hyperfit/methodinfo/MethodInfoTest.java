package org.hyperfit.methodinfo;

import org.hyperfit.annotation.FirstLink;
import org.hyperfit.annotation.Param;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.annotation.Data;
import org.hyperfit.annotation.Link;
import com.bodybuilding.commerce.hyper.client.resource.Root;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author Carlos Perez
 */
public class MethodInfoTest {

    /**
     * Exposes methods for working with a Page or resources in the Commerce Hyper API
     * @param <T> the type of the resource being paged
     */
    public interface SomeResource<T> extends HyperResource {

        @Data({"page", "number"})
        long data();

        @Link("link")
        SomeResource<T> link(
            @Param("page") Long page,
            @Param("size") Long size
        );


        @FirstLink(value="first-link", names={"name1", "name2"})
        SomeResource<T> firstLink();

        public boolean equals(Object o);
    }

    private Map<String, Method> getMethodSet(Class<?> clazz) {
        Map<String, Method> methodMap = new HashMap<String, Method>();
        Method[] methods = clazz.getMethods();
        if (methods.length > 0) {
            for (Method method : methods) {
                methodMap.put(method.getName(), method);
            }
        }
        return methodMap;
    }

    @Test
    public void testCache() {
        Class clazz = SomeResource.class;
        Map<String, Method> methodMap = getMethodSet(clazz);

        ResourceMethodInfoCache resourceMethodInfoCache = new ConcurrentHashMapResourceMethodInfoCache();
        MethodInfoCache methodInfoCache = resourceMethodInfoCache.get(clazz);

        //equals
        Method equalsMethod = methodMap.get("equals");
        MethodInfo equalsMethodInfo = methodInfoCache.get(equalsMethod);
        assertEquals(MethodInfo.MethodType.EQUALS, equalsMethodInfo.getMethodType());

        //link method
        Method linkMethod = methodMap.get("link");
        MethodInfo searchMethodInfo = methodInfoCache.get(linkMethod);

        assertNull(searchMethodInfo.getMethodType());
        assertArrayEquals(searchMethodInfo.getParameterAnnotations(), linkMethod.getParameterAnnotations());
        assertEquals(linkMethod.getReturnType(), searchMethodInfo.getReturnType());
        assertEquals(linkMethod.getGenericReturnType(), searchMethodInfo.getGenericReturnType());
        assertEquals(linkMethod.getAnnotation(Link.class), searchMethodInfo.getLinkAnnotation());
        assertNull(searchMethodInfo.getDataAnnotation());

        //data method
        Method dataMethod = methodMap.get("data");
        MethodInfo dataMethodInfo = methodInfoCache.get(dataMethod);

        assertNull(dataMethodInfo.getMethodType());
        assertEquals(dataMethod.getAnnotation(Data.class), dataMethodInfo.getDataAnnotation());
        assertNull(dataMethodInfo.getLinkAnnotation());

        //getLink method
        Method getLinkMethod = methodMap.get("getLink");
        MethodInfo getLinkMethodInfo = methodInfoCache.get(getLinkMethod);
        assertEquals(MethodInfo.MethodType.GET_LINK, getLinkMethodInfo.getMethodType());

        //hyper resource method
        Method hyperResourceMethod = methodMap.get("resolveLinkLocal");
        MethodInfo hyperResourceMethodInfo = methodInfoCache.get(hyperResourceMethod);
        assertEquals(MethodInfo.MethodType.FROM_HYPER_RESOURCE_CLASS, hyperResourceMethodInfo.getMethodType());


        //firstlinkmethod
        Method firstLinkMethod = methodMap.get("firstLink");
        MethodInfo firstLinkMethodInfo = methodInfoCache.get(firstLinkMethod);
        assertNull(firstLinkMethodInfo.getDataAnnotation());
        assertNull(firstLinkMethodInfo.getLinkAnnotation());
        assertNull(firstLinkMethodInfo.getMethodType());
        assertEquals(firstLinkMethod.getAnnotation(FirstLink.class), firstLinkMethodInfo.getFirstLinkAnnotation());

    }


    @Ignore("this isn't a unit test, but just a speed test between an implementation using a ConcurrencyMap and a sync map that locks the entire map")
    @Test
    public void testResourceMethodInfoMapCacheWithThreads()  throws InterruptedException {
        testResourceMethodInfoMapCacheWithThreads(new ConcurrentHashMapResourceMethodInfoCache());
        testResourceMethodInfoMapCacheWithThreads(new ResourceMethodInfoSyncMapCache());
    }

    private void testResourceMethodInfoMapCacheWithThreads(final ResourceMethodInfoCache resourceMethodInfoCache) throws InterruptedException {

        long averageTime = 0;

        final int THREADS = 90;

        final Set<Class<?>> classesSet = getAllClasses(Root.class);
        final List<Class<?>> classesList = new ArrayList<Class<?>>();
        classesList.addAll(classesSet);

        long time = System.nanoTime();
        for (int i = 0; i < 10; i++) {
            ExecutorService service = Executors.newFixedThreadPool(THREADS);

            for (int j = 0; j < THREADS; j++) {
                final Random r = new Random();
                service.execute(new Runnable() {
                    public void run() {
                        Class<?> randomClazz = classesList.get(r.nextInt(classesList.size()));
                        Method[] methods = randomClazz.getMethods();

                        System.out.println("Class: " + randomClazz.getSimpleName());
                        Method randomMethod = methods[r.nextInt(methods.length)];

                        MethodInfoCache methodInfoCache = resourceMethodInfoCache.get(randomClazz);

                        MethodInfo methodInfo = methodInfoCache.get(randomMethod);

                        System.out.println(methodInfo);

                    }
                });
            }
            // Make sure the executor accept no new threads.
            service.shutdown();
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            long timeUsed = (System.nanoTime() - time) / 1000000L;
            averageTime += timeUsed;
            System.out.println("------>All threads are completed in " + timeUsed + " ms\n");
        }

        System.out.println("\nThe average time is " + averageTime / 10 + " ms");
    }


    private void allClassesToSet(Class<?> clazz, Set<Class<?>> classSet) {

        Method[] methods = clazz.getMethods();
        if (methods.length > 0) {
            classSet.add(clazz);
            for (Method method : methods) {
                if (!classSet.contains(method.getReturnType())) {
                    allClassesToSet(method.getReturnType(), classSet);
                }
            }
        }
    }

    private Set<Class<?>> getAllClasses(Class<?> clazz) {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        allClassesToSet(clazz, classSet);
        return classSet;
    }


    //used to test against the actual implementation
    //locks the whole map
    public class ResourceMethodInfoSyncMapCache implements ResourceMethodInfoCache {

        private final Map<Class<?>, MethodInfoCache> resourceMethodInfoMap = new HashMap<Class<?>, MethodInfoCache>();

        public MethodInfoCache get(Class<?> clazz) {
            synchronized (resourceMethodInfoMap) {
                MethodInfoCache methodInfoCache = resourceMethodInfoMap.get(clazz);
                if (methodInfoCache == null) {
                    methodInfoCache = new MethodInfoSyncMapCache();
                    resourceMethodInfoMap.put(clazz, methodInfoCache);
                }
                return methodInfoCache;
            }
        }

        public MethodInfoCache put(Class<?> clazz, MethodInfoCache methodInfoCache) {
            synchronized (resourceMethodInfoMap) {
                return resourceMethodInfoMap.put(clazz, methodInfoCache);
            }
        }
    }

    public class MethodInfoSyncMapCache implements MethodInfoCache {

        private final Map<Method, MethodInfo> methodInfoCache = new HashMap<Method, MethodInfo>();

        public MethodInfo get(Method method) {
            synchronized (methodInfoCache) {
                MethodInfo methodInfo = methodInfoCache.get(method);
                if (methodInfo == null) {
                    methodInfo = new MethodInfo(method);
                    methodInfoCache.put(method, methodInfo);
                }

                return methodInfo;
            }
        }

        public MethodInfo put(Method method, MethodInfo methodInfo) {
            synchronized (methodInfoCache) {
                return methodInfoCache.put(method, methodInfo);
            }
        }
    }


}
