package org.hyperfit.utils;

/**
 * Object utils borrowed from Guava
 *
 */
public class MoreObjects {
    public static <T> T firstNonNull( T first,  T second) {
        return first != null?first: Preconditions.checkNotNull(second);
    }
}
