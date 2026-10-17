package io.protonaut.core.spi;

import io.protonaut.core.models.MethodDescriptorModel;
import io.protonaut.core.models.ServiceDescriptorModel;

import java.util.List;
import java.util.Optional;

public interface DescriptorRegistry {
    void registerService(ServiceDescriptorModel service);

    List<ServiceDescriptorModel> getAllServices();

    Optional<ServiceDescriptorModel> findServiceById(String serviceId);

    Optional<MethodDescriptorModel> findMethodById(String serviceId, String methodId);

    void clear();
}
