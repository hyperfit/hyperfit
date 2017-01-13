package org.hyperfit;

import lombok.NonNull;

import java.lang.reflect.Method;

/**
 * Created by btilford on 1/13/17.
 */
public interface DefaultMethodInvoker<T> {

    Object invoke(
            @NonNull final DefaultMethodContext<T> context,
            final Object[] args);

    class DefaultMethodContext<T> {
        private final Class<T>[] interfaces;
        private final HyperResourceInvokeHandler hyperHandler;
        private final T hyperProxy;
        private final Method method;

        public DefaultMethodContext(
                @NonNull Class<T>[] interfaces,
                HyperResourceInvokeHandler hyperHandler,
                T hyperProxy,
                @NonNull Method method) {
            this.interfaces = interfaces;
            this.hyperHandler = hyperHandler;
            this.hyperProxy = hyperProxy;
            this.method = method;
        }

        public Method getMethod() {
            return method;
        }

        public Class<T>[] getInterfaces() {
            return interfaces;
        }

        public HyperResourceInvokeHandler getHyperHandler() {
            return hyperHandler;
        }

        public T getHyperProxy() {
            return hyperProxy;
        }
    }
}
