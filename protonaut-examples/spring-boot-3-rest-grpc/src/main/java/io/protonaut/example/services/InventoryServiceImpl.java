package io.protonaut.example.services;

import io.grpc.stub.StreamObserver;
import io.protonaut.example.warehouse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InventoryServiceImpl extends InventoryServiceGrpc.InventoryServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final Map<String, Integer> stockLevels = new ConcurrentHashMap<>();

    public InventoryServiceImpl() {
        stockLevels.put("SKU-LAPTOP-01", 42);
        stockLevels.put("SKU-PHONE-99", 150);
        stockLevels.put("SKU-HEADPHONES-07", 78);
    }

    @Override
    public void checkStock(StockCheckRequest request, StreamObserver<StockCheckResponse> responseObserver) {
        String sku = request.getSku();
        int stock = stockLevels.getOrDefault(sku, 10);
        log.info("[gRPC:InventoryService] Checking stock for SKU='{}' -> In Stock: {}", sku, stock);

        StockCheckResponse response = StockCheckResponse.newBuilder()
                .setSku(sku)
                .setItemName("Product " + sku)
                .setInStock(stock)
                .setIsAvailable(stock > 0)
                .setUnitPrice(99.99)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reserveStock(ReserveStockRequest request, StreamObserver<ReserveStockResponse> responseObserver) {
        String sku = request.getSku();
        int qty = request.getQuantity();
        log.info("[gRPC:InventoryService] Reserving {} units for SKU='{}', OrderId='{}'", qty, sku, request.getOrderId());

        int current = stockLevels.getOrDefault(sku, 10);
        int reserved = Math.min(current, qty);
        stockLevels.put(sku, current - reserved);

        ReserveStockResponse response = ReserveStockResponse.newBuilder()
                .setReservationId("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .setSku(sku)
                .setReservedQuantity(reserved)
                .setStatus(reserved == qty ? "CONFIRMED" : "PARTIAL")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
