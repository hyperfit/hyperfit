package org.hyperfit.methodinfo;

import org.hyperfit.annotation.FirstLink;
import org.hyperfit.annotation.Param;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.annotation.Data;
import org.hyperfit.annotation.Link;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;

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


        @FirstLink(rel="first-link", names={"name1", "name2"})
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


}
