package io.protonaut.core.spi;

import io.protonaut.core.models.ProtoSourceModel;

import java.util.List;
import java.util.Optional;

public interface ProtoSourceResolver {
    /**
     * Resolves the proto source model for a given service.
     *
     * @param serviceId service identifier or full name
     * @return the proto source model (original if found, or reconstructed)
     */
    Optional<ProtoSourceModel> resolveProtoSource(String serviceId);

    /**
     * Resolves all available proto sources in the system.
     *
     * @return list of proto source models
     */
    List<ProtoSourceModel> resolveAllProtoSources();
}
