package org.kidinov.rijksmuseum.util;

/**
 * Set of events which used for notify subscribers via {@link RxEventBus}
 */
public class BusEvents {
    private BusEvents() {
    }

    public static class AuthenticationError {
    }

    public static class NetworkEnabled {
    }
}
