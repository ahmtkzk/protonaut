package io.protonaut.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.protonaut.example.warehouse.InventoryServiceGrpc;
import io.protonaut.example.warehouse.StockCheckRequest;
import io.protonaut.example.warehouse.StockCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
public class InventoryGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(InventoryGrpcClient.class);

    private final String targetHost;
    private final int targetPort;
    private ManagedChannel channel;
    private InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    public InventoryGrpcClient(
            @Value("${app.remote.inventory-grpc-host:localhost}") String targetHost,
            @Value("${app.remote.inventory-grpc-port:9090}") int targetPort
    ) {
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.channel = ManagedChannelBuilder.forAddress(targetHost, targetPort)
                .usePlaintext()
                .build();
        this.stub = InventoryServiceGrpc.newBlockingStub(channel);
        log.info("[InventoryGrpcClient] Initialized downstream gRPC client targeting {}:{}", targetHost, targetPort);
    }

    public StockCheckResponse checkDownstreamStock(String sku) {
        log.info("[InventoryGrpcClient] Initiating cross-service gRPC call -> InventoryService at {}:{}", targetHost, targetPort);
        StockCheckRequest request = StockCheckRequest.newBuilder().setSku(sku).build();

        try {
            StockCheckResponse response = stub.withDeadlineAfter(3, TimeUnit.SECONDS).checkStock(request);
            log.info("[InventoryGrpcClient] Received gRPC response: SKU='{}', InStock={}, Available={}",
                    response.getSku(), response.getInStock(), response.getIsAvailable());
            return response;
        } catch (StatusRuntimeException e) {
            log.warn("[InventoryGrpcClient] Downstream gRPC call to {}:{} failed: {} (Remote service offline)",
                    targetHost, targetPort, e.getStatus());
            return StockCheckResponse.newBuilder()
                    .setSku(sku)
                    .setItemName("Mock/Fallback Item")
                    .setInStock(999)
                    .setIsAvailable(true)
                    .setUnitPrice(49.99)
                    .build();
        } catch (Exception e) {
            log.error("[InventoryGrpcClient] Error calling downstream service: {}", e.getMessage());
            return StockCheckResponse.newBuilder()
                    .setSku(sku)
                    .setItemName("Offline Fallback Item")
                    .setInStock(0)
                    .setIsAvailable(false)
                    .build();
        }
    }

    @PreDestroy
    public void cleanup() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}
