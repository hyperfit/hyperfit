package org.hyperfit.java8;

import org.hyperfit.exception.HyperfitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.stream.Stream;


public class Java8DefaultMethodHandler implements org.hyperfit.handlers.Java8DefaultMethodHandler {
    private static final Logger LOG = LoggerFactory.getLogger(Java8DefaultMethodHandler.class);

    // We need the lookup that has private access to instantiate the interface with MethodHandles.Lookup.PRIVATE
    private static final Constructor<MethodHandles.Lookup> LOOKUP_CONSTRUCTOR;

    static {

        try {
            LOOKUP_CONSTRUCTOR = MethodHandles.Lookup.class.getDeclaredConstructor(
                Class.class,
                int.class
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        LOOKUP_CONSTRUCTOR.setAccessible(true);
    }


    @Override
    public Object invoke(
        final DefaultMethodContext context,
        final Object[] args
    ) {

        return Stream.of(context.getInterfaces())
            .filter(tClass -> {
                try {
                    if(tClass.isAssignableFrom(context.getHyperProxy().getClass())) {
                        //Make sure the method exists. TODO: is there a better way that doesn't throw?
                        tClass.getMethod(context.getMethod().getName(), context.getMethod().getParameterTypes());
                        return true;
                    }
                } catch (NoSuchMethodException e) {
                    LOG.warn(e.getMessage(), e);
                }

                return false;
            })
            .findFirst()
            .map(t -> Proxy.newProxyInstance(
                    t.getClassLoader(),
                    new Class[]{t},
                    (proxy, method, params) -> {
                        //This technique was stolen from https://zeroturnaround.com/rebellabs/recognize-and-conquer-java-proxies-default-methods-and-method-handles/
                        // 1. Instantiate the MethodHandle.Lookup that has private access
                        // 2. Builder method handle that doesn't check for overrides and will be able
                        // to call the default method on the interface. `unreflectSpecial()`
                        // 3. Bind to the Proxy created above/passed in
                        // 4. Invoke the method handle with the args.
                        return LOOKUP_CONSTRUCTOR.newInstance(t, MethodHandles.Lookup.PRIVATE)
                            .unreflectSpecial(method, t)
                            .bindTo(context.getHyperProxy())
                            .invokeWithArguments(params);
                    }
                )
            )
            .map(
                i -> {
                    try {
                        return context.getMethod().invoke(i, args);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                       throw new RuntimeException(e);
                    }
                }
            )
            .orElseThrow(
                () -> new HyperfitException(
                    "No interface in {} has default method {}!",
                    context.getInterfaces(),
                    context.getMethod().toString()
                )
            )
            ;

    }


}
