package org.hyperfit.jdk8;

import org.hyperfit.DefaultMethodInvoker;
import org.hyperfit.exception.HyperfitException;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by btilford on 1/13/17.
 */
@MetaInfServices
public class Jdk8DefaultMethodInvoker<T> implements DefaultMethodInvoker<T> {
    private static final Logger LOG = LoggerFactory.getLogger(Jdk8DefaultMethodInvoker.class);


    @Override
    public Object invoke(
            final DefaultMethodContext<T> context,
            final Object[] args) {

        try {
            Optional<Class<?>> targetType = Stream.of(context.getInterfaces()).filter(tClass -> {
                try {
                    if(tClass.isAssignableFrom(context.getHyperProxy().getClass())) {
                        tClass.getMethod(context.getMethod().getName(), context.getMethod().getParameterTypes());
                        return true;
                    }
                    else {
                        return false;
                    }
                } catch (NoSuchMethodException e) {
                    LOG.warn(e.getMessage(), e);
                    return false;
                }
            }).findFirst();

            if (targetType.isPresent()) {
                T instance = ((Class<T>)targetType.get()).cast(
                        Proxy.newProxyInstance(
                                Thread.currentThread().getContextClassLoader(),
                                new Class[]{targetType.get()},
                                (proxy, method, params) -> {
                                    if(method.isDefault()) {
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
                                        return constructor.newInstance(targetType.get(), MethodHandles.Lookup.PRIVATE)
                                                .unreflectSpecial(method, targetType.get())
                                                .bindTo(proxy)
                                                .invokeWithArguments(params);
                                    }
                                    else {
                                        return context.getHyperHandler().invoke(context.getHyperProxy(), method, args);
                                    }
                                }
                        )
                );
                // Invoke the custom proxy able to invoke default methods.
                return context.getMethod().invoke(instance, args);
            } else {
                throw new HyperfitException(
                        "No interface in {} has default method {}!",
                        context.getInterfaces(),
                        context.getMethod().toString()
                );
            }

        } catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
            return null;
        } catch (InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }

    }


}
