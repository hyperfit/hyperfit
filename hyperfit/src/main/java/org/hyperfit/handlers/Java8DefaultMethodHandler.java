package org.hyperfit.handlers;

import lombok.NonNull;
import org.hyperfit.resource.HyperResource;

import java.lang.reflect.Method;

/**
 * Defines an interface for invoking java8 default methods in a java6 safe way
 */
public interface Java8DefaultMethodHandler {

    Object invoke(
        @NonNull final DefaultMethodContext context,
        final Object[] args
    );

    class DefaultMethodContext {
        private final Class<?>[] interfaces;
        private final HyperResource hyperResource;
        private final Method method;

        public DefaultMethodContext(
            HyperResource hyperResource,
            @NonNull Method method
        ) {
            this.interfaces = hyperResource.getClass().getInterfaces();
            this.hyperResource = hyperResource;
            this.method = method;
        }

        public Method getMethod() {
            return method;
        }

        public Class<?>[] getInterfaces() {
            return interfaces;
        }


        public HyperResource getHyperResource() {
            return hyperResource;
        }
    }
}
