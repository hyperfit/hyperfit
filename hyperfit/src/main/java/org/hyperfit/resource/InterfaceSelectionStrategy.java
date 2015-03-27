package org.hyperfit.resource;

public interface InterfaceSelectionStrategy {
    Class<?>[] determineInterfaces(Class<?> expectedInterface, HyperResource resourceToWrap);
}
