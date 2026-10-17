package io.protonaut.autoconfigure;

import com.google.protobuf.Descriptors;
import io.grpc.*;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.protonaut.core.models.ServiceDescriptorModel;
import io.protonaut.core.spi.DescriptorRegistry;
import io.protonaut.grpc.descriptors.ProtobufDescriptorConverter;
import io.protonaut.grpc.descriptors.ServiceDescriptorExtractor;
import io.protonaut.grpc.discovery.DiscoveryFilter;
import io.protonaut.grpc.execution.DynamicGrpcExecutor;
import io.protonaut.grpc.protobuf.ClasspathProtoSourceResolver;
import io.protonaut.web.netty.ProtonautServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;
import java.util.*;

public class ProtonautLifecycleManager implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(ProtonautLifecycleManager.class);

    private final ApplicationContext applicationContext;
    private final ProtonautProperties properties;
    private final DescriptorRegistry descriptorRegistry;
    private final DynamicGrpcExecutor executor;
    private final ClasspathProtoSourceResolver protoSourceResolver;
    private final ProtonautServer server;

    private boolean running = false;
    private Server inProcessServer;
    private ManagedChannel inProcessChannel;

    public ProtonautLifecycleManager(
            ApplicationContext applicationContext,
            ProtonautProperties properties,
            DescriptorRegistry descriptorRegistry,
            DynamicGrpcExecutor executor,
            ClasspathProtoSourceResolver protoSourceResolver,
            ProtonautServer server
    ) {
        this.applicationContext = applicationContext;
        this.properties = properties;
        this.descriptorRegistry = descriptorRegistry;
        this.executor = executor;
        this.protoSourceResolver = protoSourceResolver;
        this.server = server;
    }

    @Override
    public void start() {
        if (running) return;

        log.info("[Protonaut] Discovering gRPC services from Spring Boot application context...");

        // 1. Discover all BindableService and ServerServiceDefinition beans
        Map<String, BindableService> bindableServices = applicationContext.getBeansOfType(BindableService.class);
        Map<String, ServerServiceDefinition> serviceDefinitions = applicationContext.getBeansOfType(ServerServiceDefinition.class);

        List<ServerServiceDefinition> allDefinitions = new ArrayList<>(serviceDefinitions.values());
        for (BindableService bs : bindableServices.values()) {
            allDefinitions.add(bs.bindService());
        }

        if (allDefinitions.isEmpty()) {
            log.warn("[Protonaut] DIAGNOSTIC: No gRPC service beans (BindableService or ServerServiceDefinition) were detected in the ApplicationContext. Protonaut is active and waiting for gRPC service registrations.");
        } else {
            // Build an in-process server for safe, ultra-low-latency direct dispatching without SSRF risks!
            String inProcessServerName = "protonaut-inprocess-" + UUID.randomUUID();
            InProcessServerBuilder serverBuilder = InProcessServerBuilder.forName(inProcessServerName)
                    .directExecutor();

            DiscoveryFilter filter = new DiscoveryFilter(
                    properties.getDiscovery().getIncludeServices(),
                    properties.getDiscovery().getExcludeServices(),
                    properties.getDiscovery().getIncludePackages(),
                    properties.getDiscovery().getExcludePackages(),
                    properties.getDiscovery().isReflectionEnabled()
            );

            for (ServerServiceDefinition def : allDefinitions) {
                String fullName = def.getServiceDescriptor().getName();
                int lastDot = fullName.lastIndexOf('.');
                String pkg = lastDot > 0 ? fullName.substring(0, lastDot) : "";
                String simpleName = lastDot > 0 ? fullName.substring(lastDot + 1) : fullName;

                if (!filter.isServiceAllowed(fullName, pkg, simpleName)) {
                    log.debug("[Protonaut] Excluded service: {}", fullName);
                    continue;
                }

                serverBuilder.addService(def);

                Descriptors.ServiceDescriptor protoService = ServiceDescriptorExtractor.extract(def);
                if (protoService != null) {
                    ServiceDescriptorModel model = ProtobufDescriptorConverter.toServiceModel(protoService);
                    descriptorRegistry.registerService(model);
                    executor.registerService(protoService);
                    protoSourceResolver.registerService(protoService);
                    log.info("[Protonaut] Discovered service '{}' ({} methods)", model.fullName(), model.methods().size());
                } else {
                    log.warn("[Protonaut] Protobuf descriptors could not be resolved for service '{}'", fullName);
                }
            }

            try {
                inProcessServer = serverBuilder.build().start();
                inProcessChannel = InProcessChannelBuilder.forName(inProcessServerName)
                        .directExecutor()
                        .build();
                executor.setChannel(inProcessChannel);
                log.info("[Protonaut] Local in-process gRPC execution channel established for direct zero-latency invocation.");
            } catch (IOException e) {
                log.error("[Protonaut] Error starting in-process server: {}", e.getMessage(), e);
            }
        }

        // 2. Start Protonaut Web Server
        if (server != null) {
            server.start();
        }

        running = true;
    }

    @Override
    public void stop() {
        if (!running) return;

        if (server != null) {
            server.stop();
        }

        if (inProcessChannel != null) {
            inProcessChannel.shutdownNow();
            inProcessChannel = null;
        }

        if (inProcessServer != null) {
            inProcessServer.shutdownNow();
            inProcessServer = null;
        }

        executor.setChannel(null);

        running = false;
        log.info("[Protonaut] Auto-configured components stopped cleanly.");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 100; // Start after gRPC services have initialized
    }
}
