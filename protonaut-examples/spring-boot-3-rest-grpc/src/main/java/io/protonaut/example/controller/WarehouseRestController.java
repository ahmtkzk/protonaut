package io.protonaut.example.controller;

import io.protonaut.example.client.PaymentGrpcClient;
import io.protonaut.example.payment.PaymentResponse;
import io.protonaut.example.services.InventoryServiceImpl;
import io.protonaut.example.warehouse.ReserveStockRequest;
import io.protonaut.example.warehouse.ReserveStockResponse;
import io.protonaut.example.warehouse.StockCheckRequest;
import io.protonaut.example.warehouse.StockCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/warehouse")
@Tag(name = "Warehouse Operations", description = "REST APIs interoperating directly with local and remote gRPC microservices")
public class WarehouseRestController {

    private final InventoryServiceImpl inventoryService;
    private final PaymentGrpcClient paymentClient;

    public WarehouseRestController(InventoryServiceImpl inventoryService, PaymentGrpcClient paymentClient) {
        this.inventoryService = inventoryService;
        this.paymentClient = paymentClient;
    }

    @GetMapping("/stock/{sku}")
    @Operation(summary = "Check Inventory Stock via Local gRPC Service", description = "Invokes the local gRPC InventoryService in-process to verify stock level for a SKU.")
    @ApiResponse(responseCode = "200", description = "Stock information successfully retrieved")
    public ResponseEntity<Map<String, Object>> getStock(
            @PathVariable @Parameter(description = "SKU item code", example = "SKU-LAPTOP-01") String sku) {

        CompletableFuture<StockCheckResponse> future = new CompletableFuture<>();
        inventoryService.checkStock(
                StockCheckRequest.newBuilder().setSku(sku).build(),
                new io.grpc.stub.StreamObserver<>() {
                    @Override
                    public void onNext(StockCheckResponse val) {
                        future.complete(val);
                    }
                    @Override
                    public void onError(Throwable t) {
                        future.completeExceptionally(t);
                    }
                    @Override
                    public void onCompleted() {}
                }
        );

        try {
            StockCheckResponse resp = future.get();
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("sku", resp.getSku());
            body.put("itemName", resp.getItemName());
            body.put("inStock", resp.getInStock());
            body.put("available", resp.getIsAvailable());
            body.put("unitPrice", resp.getUnitPrice());
            body.put("servedBy", "Local gRPC InventoryService");
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/checkout")
    @Operation(summary = "Order Checkout & Cross-Service gRPC Payment",
            description = "Reserves local stock via gRPC AND initiates a cross-service remote gRPC call to PaymentService on port 6565.")
    @ApiResponse(responseCode = "200", description = "Order processed successfully across services")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody OrderCheckoutDto request) {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 1. Reserve Stock locally via gRPC
        CompletableFuture<ReserveStockResponse> reserveFuture = new CompletableFuture<>();
        inventoryService.reserveStock(
                ReserveStockRequest.newBuilder()
                        .setOrderId(orderId)
                        .setSku(request.sku())
                        .setQuantity(request.quantity())
                        .build(),
                new io.grpc.stub.StreamObserver<>() {
                    @Override
                    public void onNext(ReserveStockResponse val) {
                        reserveFuture.complete(val);
                    }
                    @Override
                    public void onError(Throwable t) {
                        reserveFuture.completeExceptionally(t);
                    }
                    @Override
                    public void onCompleted() {}
                }
        );

        ReserveStockResponse reserveResp;
        try {
            reserveResp = reserveFuture.get();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to reserve stock: " + e.getMessage()));
        }

        // 2. Cross-service gRPC call to PaymentService on port 6565!
        PaymentResponse paymentResp = paymentClient.executeRemotePayment(
                orderId,
                request.customerId(),
                request.amount(),
                request.currency() != null ? request.currency() : "USD"
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", orderId);
        result.put("reservation", Map.of(
                "reservationId", reserveResp.getReservationId(),
                "sku", reserveResp.getSku(),
                "reservedQuantity", reserveResp.getReservedQuantity(),
                "status", reserveResp.getStatus()
        ));
        result.put("remotePaymentGrpc", Map.of(
                "transactionId", paymentResp.getTransactionId(),
                "status", paymentResp.getStatus(),
                "message", paymentResp.getMessage(),
                "targetGrpcPort", 6565
        ));
        result.put("architectureNote", "Cross-service interoperability: REST endpoint invoked local Inventory gRPC and remote Payment gRPC (port 6565).");

        return ResponseEntity.ok(result);
    }

    public record OrderCheckoutDto(
            String customerId,
            String sku,
            int quantity,
            double amount,
            String currency
    ) {}
}
