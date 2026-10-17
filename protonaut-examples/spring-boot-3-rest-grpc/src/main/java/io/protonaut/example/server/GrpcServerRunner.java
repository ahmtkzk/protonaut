package io.protonaut.example.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class GrpcServerRunner implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(GrpcServerRunner.class);

    private final int port;
    private final List<BindableService> services;
    private Server server;
    private boolean running = false;

    public GrpcServerRunner(
            @Value("${app.grpc.port:9090}") int port,
            List<BindableService> services
    ) {
        this.port = port;
        this.services = services;
    }

    @Override
    public void start() {
        if (running) return;

        try {
            ServerBuilder<?> builder = ServerBuilder.forPort(port);
            for (BindableService svc : services) {
                builder.addService(svc);
                log.info("[GrpcServerRunner] Bound service '{}' to TCP port {}", svc.bindService().getServiceDescriptor().getName(), port);
            }
            server = builder.build().start();
            running = true;
            log.info("[GrpcServerRunner] External gRPC server listening on TCP port {}", port);
        } catch (IOException e) {
            log.error("[GrpcServerRunner] Failed to start gRPC server on port {}: {}", port, e.getMessage());
        }
    }

    @Override
    public void stop() {
        if (!running) return;
        if (server != null) {
            server.shutdown();
        }
        running = false;
        log.info("[GrpcServerRunner] Stopped gRPC server on port {}", port);
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
