package org.hyperfit.jdk8;

import org.hyperfit.DefaultMethodInvoker;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by btilford on 1/13/17.
 */
@MetaInfServices
public class Jdk8DefaultMethodInvoker<T> implements DefaultMethodInvoker<T> {
    private static final Logger LOG = LoggerFactory.getLogger(Jdk8DefaultMethodInvoker.class);


    @Override
    public Object invoke(final Class<T> targetType, final Method method, final Object[] args) {

        try {
            T instance = targetType.cast(
                    Proxy.newProxyInstance(
                            Thread.currentThread().getContextClassLoader(),
                            new Class[]{targetType},
                            (proxy1, method1, args1) -> {
                                // We need a lookup that has private access to instantiate the interface
                                Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(
                                        Class.class,
                                        int.class
                                );
                                constructor.setAccessible(true);

                                // 1. Instantiate the MethodHandle.Lookup that has private access
                                // 2. Builder method handle that doesn't check for overrides and will be able
                                // to call the default method on the interface. `unreflectSpecial()`
                                // 3. Bind to the Proxy created above/passed in
                                // 4. Invoke the method handle with the args.
                                return constructor.newInstance(targetType, MethodHandles.Lookup.PRIVATE)
                                        .unreflectSpecial(method1, targetType)
                                        .bindTo(proxy1)
                                        .invokeWithArguments(args1);
                            }
                    )
            );
            // Invoke the custom proxy able to invoke default methods.
            return method.invoke(instance, args);

        } catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
            return null;
        } catch (InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }


    }
}
