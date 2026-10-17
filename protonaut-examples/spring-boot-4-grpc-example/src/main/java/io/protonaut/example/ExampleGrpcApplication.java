package io.protonaut.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.protonaut.example.services.OrderServiceImpl;
import io.protonaut.example.services.ProductServiceImpl;
import io.protonaut.example.services.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class ExampleGrpcApplication {
    private static final Logger log = LoggerFactory.getLogger(ExampleGrpcApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ExampleGrpcApplication.class, args);
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Server grpcServer(
            @Value("${grpc.server.port:9090}") int port,
            UserServiceImpl userService,
            OrderServiceImpl orderService,
            ProductServiceImpl productService
    ) throws IOException {
        log.info("[ExampleApp] Starting business gRPC server on port {}", port);
        return ServerBuilder.forPort(port)
                .addService(userService)
                .addService(orderService)
                .addService(productService)
                .build();
    }
}
