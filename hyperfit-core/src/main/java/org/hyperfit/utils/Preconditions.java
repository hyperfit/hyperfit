package org.hyperfit.utils;

/**
 * Assert Preconditions - Borrowed from Guava
 */
public class Preconditions {
    /**
     * Verifies that the given object reference is not {@code null}.
     *
     * @param reference the given object reference.
     * @return the non-{@code null} reference that was validated.
     * @throws NullPointerException if the given object reference is {@code null}.
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Verifies that the given object reference is not {@code null}.
     *
     * @param reference the given object reference.
     * @param message error message in case of null reference.
     * @return the non-{@code null} reference that was validated.
     * @throws NullPointerException if the given object reference is {@code null}.
     */
    public static <T> T checkNotNull(T reference, String message) {
        if (reference == null) {
            throw new NullPointerException(message);
        }
        return reference;
    }
}
