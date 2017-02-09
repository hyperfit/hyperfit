package org.hyperfit.handlers;

import lombok.NonNull;
import org.hyperfit.HyperResourceInvokeHandler;

import java.lang.reflect.Method;

/**
 * Exposes a method for invoking java8 default methods in a java6 safe way
 */
public interface Java8DefaultMethodHandler {

    Object invoke(
        @NonNull final DefaultMethodContext context,
        final Object[] args
    );

    class DefaultMethodContext {
        private final Class<?>[] interfaces;
        private final Object hyperProxy;
        private final Method method;

        public DefaultMethodContext(
            Object hyperProxy,
            @NonNull Method method
        ) {
            this.interfaces = hyperProxy.getClass().getInterfaces();
            this.hyperProxy = hyperProxy;
            this.method = method;
        }

        public Method getMethod() {
            return method;
        }

        public Class<?>[] getInterfaces() {
            return interfaces;
        }


        public Object getHyperProxy() {
            return hyperProxy;
        }
    }
}
