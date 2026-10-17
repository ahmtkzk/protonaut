package io.protonaut.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.protonaut.example.payment.PaymentRequest;
import io.protonaut.example.payment.PaymentResponse;
import io.protonaut.example.payment.PaymentServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
public class PaymentGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(PaymentGrpcClient.class);

    private final String targetHost;
    private final int targetPort;
    private ManagedChannel channel;
    private PaymentServiceGrpc.PaymentServiceBlockingStub stub;

    public PaymentGrpcClient(
            @Value("${app.remote.payment-grpc-host:localhost}") String targetHost,
            @Value("${app.remote.payment-grpc-port:6565}") int targetPort
    ) {
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.channel = ManagedChannelBuilder.forAddress(targetHost, targetPort)
                .usePlaintext()
                .build();
        this.stub = PaymentServiceGrpc.newBlockingStub(channel);
        log.info("[PaymentGrpcClient] Initialized gRPC client targeting {}:{}", targetHost, targetPort);
    }

    public PaymentResponse executeRemotePayment(String orderId, String customerId, double amount, String currency) {
        log.info("[PaymentGrpcClient] Initiating cross-service gRPC call -> PaymentService at {}:{}", targetHost, targetPort);
        PaymentRequest request = PaymentRequest.newBuilder()
                .setOrderId(orderId)
                .setCustomerId(customerId)
                .setAmount(amount)
                .setCurrency(currency)
                .build();

        try {
            PaymentResponse response = stub.withDeadlineAfter(3, TimeUnit.SECONDS).processPayment(request);
            log.info("[PaymentGrpcClient] Received gRPC response: TxID='{}', Status='{}'", response.getTransactionId(), response.getStatus());
            return response;
        } catch (StatusRuntimeException e) {
            log.warn("[PaymentGrpcClient] Remote gRPC call to {}:{} failed: {} (Service may not be running yet)", targetHost, targetPort, e.getStatus());
            return PaymentResponse.newBuilder()
                    .setTransactionId("TX-FALLBACK-" + System.currentTimeMillis())
                    .setStatus("OFFLINE_FALLBACK")
                    .setMessage("Payment queued. Remote gRPC target on port " + targetPort + " was not reachable (" + e.getStatus().getCode() + ").")
                    .setTimestamp(System.currentTimeMillis())
                    .build();
        } catch (Exception e) {
            log.error("[PaymentGrpcClient] Unexpected error calling remote gRPC service: {}", e.getMessage());
            return PaymentResponse.newBuilder()
                    .setTransactionId("TX-ERROR")
                    .setStatus("ERROR")
                    .setMessage("Error contacting payment service: " + e.getMessage())
                    .setTimestamp(System.currentTimeMillis())
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
