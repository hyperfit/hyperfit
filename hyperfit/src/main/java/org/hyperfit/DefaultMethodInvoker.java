package org.hyperfit;

import lombok.NonNull;

import java.lang.reflect.Method;

/**
 * Created by btilford on 1/13/17.
 */
public interface DefaultMethodInvoker<T> {

    Object invoke(@NonNull final Class<T>[] targetType, @NonNull final Method method, final Object[] args);

}
