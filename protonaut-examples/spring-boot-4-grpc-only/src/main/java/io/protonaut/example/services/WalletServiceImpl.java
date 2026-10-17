package io.protonaut.example.services;

import io.grpc.stub.StreamObserver;
import io.protonaut.example.payment.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WalletServiceImpl extends WalletServiceGrpc.WalletServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final Map<String, Double> balances = new ConcurrentHashMap<>();

    public WalletServiceImpl() {
        balances.put("CUST-100", 5420.50);
        balances.put("CUST-200", 12500.00);
        balances.put("CUST-300", 350.75);
    }

    @Override
    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        String customerId = request.getCustomerId();
        double balance = balances.getOrDefault(customerId, 1000.0);
        log.info("[gRPC:WalletService:Port6565] Balance query for CustomerId='{}' -> {}", customerId, balance);

        BalanceResponse response = BalanceResponse.newBuilder()
                .setCustomerId(customerId)
                .setAvailableBalance(balance)
                .setCurrency("USD")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void transferFunds(TransferRequest request, StreamObserver<TransferResponse> responseObserver) {
        String from = request.getFromCustomerId();
        String to = request.getToCustomerId();
        double amount = request.getAmount();

        log.info("[gRPC:WalletService:Port6565] Transferring {} from '{}' to '{}'", amount, from, to);

        double fromBal = balances.getOrDefault(from, 1000.0);
        if (fromBal < amount) {
            TransferResponse response = TransferResponse.newBuilder()
                    .setTransferId("TX-FAILED")
                    .setStatus("INSUFFICIENT_FUNDS")
                    .setRemainingBalance(fromBal)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        balances.put(from, fromBal - amount);
        balances.put(to, balances.getOrDefault(to, 0.0) + amount);

        TransferResponse response = TransferResponse.newBuilder()
                .setTransferId("TR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .setStatus("COMPLETED")
                .setRemainingBalance(fromBal - amount)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
