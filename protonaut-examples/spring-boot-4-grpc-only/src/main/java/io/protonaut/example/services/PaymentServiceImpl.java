package io.protonaut.example.services;

import io.grpc.stub.StreamObserver;
import io.protonaut.example.client.InventoryGrpcClient;
import io.protonaut.example.payment.*;
import io.protonaut.example.warehouse.StockCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final InventoryGrpcClient inventoryClient;

    public PaymentServiceImpl(InventoryGrpcClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @Override
    public void processPayment(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        log.info("[gRPC:PaymentService:Port6565] Received payment request: OrderId='{}', CustomerId='{}', Amount={} {}",
                request.getOrderId(), request.getCustomerId(), request.getAmount(), request.getCurrency());

        // Perform cross-service gRPC check to Demo 1 (InventoryService on port 9090)
        StockCheckResponse stockResp = inventoryClient.checkDownstreamStock("SKU-LAPTOP-01");
        log.info("[gRPC:PaymentService] Downstream stock verification status: SKU='{}', Available={}",
                stockResp.getSku(), stockResp.getIsAvailable());

        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        PaymentResponse response = PaymentResponse.newBuilder()
                .setTransactionId(txId)
                .setStatus("SUCCESS")
                .setMessage("Payment of " + request.getAmount() + " " + request.getCurrency() + " captured successfully. Verified with downstream inventory.")
                .setTimestamp(System.currentTimeMillis())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void refundPayment(RefundRequest request, StreamObserver<RefundResponse> responseObserver) {
        log.info("[gRPC:PaymentService:Port6565] Processing refund for TxId='{}', Amount={}",
                request.getTransactionId(), request.getAmount());

        RefundResponse response = RefundResponse.newBuilder()
                .setRefundId("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .setStatus("REFUNDED")
                .setMessage("Amount " + request.getAmount() + " refunded: " + request.getReason())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
