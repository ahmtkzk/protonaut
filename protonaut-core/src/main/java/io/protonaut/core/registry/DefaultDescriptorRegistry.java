package io.protonaut.core.registry;

import io.protonaut.core.models.MethodDescriptorModel;
import io.protonaut.core.models.ServiceDescriptorModel;
import io.protonaut.core.spi.DescriptorRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDescriptorRegistry implements DescriptorRegistry {
    private final Map<String, ServiceDescriptorModel> servicesById = new ConcurrentHashMap<>();

    @Override
    public void registerService(ServiceDescriptorModel service) {
        if (service != null && service.id() != null) {
            servicesById.put(service.id(), service);
            // Also index by full name
            if (service.fullName() != null) {
                servicesById.putIfAbsent(service.fullName(), service);
            }
        }
    }

    @Override
    public List<ServiceDescriptorModel> getAllServices() {
        // Return unique services by id
        return servicesById.values().stream()
                .distinct()
                .toList();
    }

    @Override
    public Optional<ServiceDescriptorModel> findServiceById(String serviceId) {
        if (serviceId == null) return Optional.empty();
        ServiceDescriptorModel model = servicesById.get(serviceId);
        if (model != null) {
            return Optional.of(model);
        }
        // Search by name ignore case
        return servicesById.values().stream()
                .filter(s -> s.id().equalsIgnoreCase(serviceId) ||
                             s.name().equalsIgnoreCase(serviceId) ||
                             s.fullName().equalsIgnoreCase(serviceId))
                .findFirst();
    }

    @Override
    public Optional<MethodDescriptorModel> findMethodById(String serviceId, String methodId) {
        if (methodId == null) return Optional.empty();
        if (serviceId != null && !serviceId.isBlank()) {
            Optional<ServiceDescriptorModel> svc = findServiceById(serviceId);
            if (svc.isPresent()) {
                Optional<MethodDescriptorModel> found = svc.get().methods().stream()
                        .filter(m -> m.id().equalsIgnoreCase(methodId) ||
                                     m.name().equalsIgnoreCase(methodId) ||
                                     m.fullMethodName().equalsIgnoreCase(methodId))
                        .findFirst();
                if (found.isPresent()) return found;
            }
        }
        // Fallback: search across all services
        return getAllServices().stream()
                .flatMap(s -> s.methods().stream())
                .filter(m -> m.id().equalsIgnoreCase(methodId) ||
                             m.name().equalsIgnoreCase(methodId) ||
                             m.fullMethodName().equalsIgnoreCase(methodId))
                .findFirst();
    }

    @Override
    public void clear() {
        servicesById.clear();
    }
}
