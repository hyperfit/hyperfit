package org.hyperfit.resource;

public class SimpleInterfaceSelectionStrategy implements InterfaceSelectionStrategy {

    public Class<?>[] determineInterfaces(Class<?> expectedInterface, HyperResource resourceToWrap) {
        return new Class<?>[]{expectedInterface};
    }
}
