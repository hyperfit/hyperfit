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
        private final Class<?>[] interfaces;
        private final HyperResourceInvokeHandler hyperHandler;
        private final T hyperProxy;
        private final Method method;

        public DefaultMethodContext(
                HyperResourceInvokeHandler hyperHandler,
                T hyperProxy,
                @NonNull Method method) {
            this.interfaces = hyperProxy.getClass().getInterfaces();
            this.hyperHandler = hyperHandler;
            this.hyperProxy = hyperProxy;
            this.method = method;
        }

        public Method getMethod() {
            return method;
        }

        public Class<?>[] getInterfaces() {
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
