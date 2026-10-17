package io.protonaut.grpc.discovery;

import com.google.protobuf.Descriptors;
import io.grpc.ServerServiceDefinition;
import io.protonaut.core.models.ServiceDescriptorModel;
import io.protonaut.core.spi.DescriptorRegistry;
import io.protonaut.core.spi.ServiceDiscoveryProvider;
import io.protonaut.grpc.descriptors.ProtobufDescriptorConverter;
import io.protonaut.grpc.descriptors.ServiceDescriptorExtractor;
import io.protonaut.grpc.execution.DynamicGrpcExecutor;
import io.protonaut.grpc.protobuf.ClasspathProtoSourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ServerServiceDefinitionDiscoveryProvider implements ServiceDiscoveryProvider {
    private static final Logger log = LoggerFactory.getLogger(ServerServiceDefinitionDiscoveryProvider.class);

    private final Supplier<List<ServerServiceDefinition>> serviceDefinitionsSupplier;
    private final DiscoveryFilter filter;
    private final DescriptorRegistry descriptorRegistry;
    private final DynamicGrpcExecutor executor;
    private final ClasspathProtoSourceResolver protoSourceResolver;

    public ServerServiceDefinitionDiscoveryProvider(
            Supplier<List<ServerServiceDefinition>> serviceDefinitionsSupplier,
            DiscoveryFilter filter,
            DescriptorRegistry descriptorRegistry,
            DynamicGrpcExecutor executor,
            ClasspathProtoSourceResolver protoSourceResolver
    ) {
        this.serviceDefinitionsSupplier = serviceDefinitionsSupplier;
        this.filter = filter != null ? filter : DiscoveryFilter.defaultFilter();
        this.descriptorRegistry = descriptorRegistry;
        this.executor = executor;
        this.protoSourceResolver = protoSourceResolver;
    }

    @Override
    public List<ServiceDescriptorModel> discoverServices() {
        List<ServiceDescriptorModel> discovered = new ArrayList<>();
        List<ServerServiceDefinition> definitions = serviceDefinitionsSupplier.get();
        if (definitions == null || definitions.isEmpty()) {
            log.info("No ServerServiceDefinition instances found to discover");
            return discovered;
        }

        for (ServerServiceDefinition def : definitions) {
            String fullName = def.getServiceDescriptor().getName();
            int lastDot = fullName.lastIndexOf('.');
            String pkg = lastDot > 0 ? fullName.substring(0, lastDot) : "";
            String simpleName = lastDot > 0 ? fullName.substring(lastDot + 1) : fullName;

            if (!filter.isServiceAllowed(fullName, pkg, simpleName)) {
                log.debug("Service {} filtered out by Protonaut discovery filter", fullName);
                continue;
            }

            Descriptors.ServiceDescriptor protoService = ServiceDescriptorExtractor.extract(def);
            if (protoService != null) {
                ServiceDescriptorModel model = ProtobufDescriptorConverter.toServiceModel(protoService);
                discovered.add(model);

                if (descriptorRegistry != null) {
                    descriptorRegistry.registerService(model);
                }
                if (executor != null) {
                    executor.registerService(protoService);
                }
                if (protoSourceResolver != null) {
                    protoSourceResolver.registerService(protoService);
                }

                log.info("Discovered and registered gRPC service: {} ({} methods)", model.fullName(), model.methods().size());
            } else {
                log.warn("Could not resolve protobuf descriptors for gRPC service: {}", fullName);
            }
        }

        return discovered;
    }
}
