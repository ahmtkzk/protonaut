package io.protonaut.core.spi;

import io.protonaut.core.models.ServiceDescriptorModel;
import java.util.List;

public interface ServiceDiscoveryProvider {
    /**
     * Discover all services available in the application runtime.
     *
     * @return list of discovered service models
     */
    List<ServiceDescriptorModel> discoverServices();

    /**
     * Priority of this discovery provider. Higher numbers run first.
     */
    default int getOrder() {
        return 0;
    }
}
