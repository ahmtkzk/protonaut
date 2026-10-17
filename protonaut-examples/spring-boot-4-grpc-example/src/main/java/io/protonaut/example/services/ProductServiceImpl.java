package io.protonaut.example.services;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.protonaut.example.product.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private final Map<String, Product> productsById = new ConcurrentHashMap<>();

    public ProductServiceImpl() {
        seedSampleProducts();
    }

    private void seedSampleProducts() {
        Product p1 = Product.newBuilder()
                .setId("prod-001")
                .setSku("GATEWAY-X1")
                .setName("High-Performance gRPC Gateway")
                .setDescription("Ultra-low latency ingress proxy supporting HTTP/2, gRPC-Web, and gRPC transcoding.")
                .setPrice(499.00)
                .setCurrency("USD")
                .setInStock(true)
                .setStockQuantity(150)
                .putAttributes("version", "2.5.0")
                .putAttributes("architecture", "arm64,x86_64")
                .addCategories("networking")
                .addCategories("infrastructure")
                .build();

        Product p2 = Product.newBuilder()
                .setId("prod-002")
                .setSku("AGENT-OBS-01")
                .setName("Cloud Observability Agent")
                .setDescription("Real-time distributed tracing, metrics, and OpenTelemetry collector for microservices.")
                .setPrice(199.00)
                .setCurrency("USD")
                .setInStock(true)
                .setStockQuantity(500)
                .putAttributes("protocol", "opentelemetry")
                .addCategories("observability")
                .addCategories("monitoring")
                .build();

        Product p3 = Product.newBuilder()
                .setId("prod-003")
                .setSku("SEC-VAULT-PRO")
                .setName("Hardware Security Vault Integration")
                .setDescription("Zero-trust mTLS encryption module with hardware security module support.")
                .setPrice(1299.00)
                .setCurrency("USD")
                .setInStock(false)
                .setStockQuantity(0)
                .putAttributes("security_level", "fips-140-2")
                .addCategories("security")
                .build();

        productsById.put(p1.getId(), p1);
        productsById.put(p2.getId(), p2);
        productsById.put(p3.getId(), p3);
    }

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<Product> responseObserver) {
        Product product = productsById.get(request.getProductId());
        if (product != null) {
            responseObserver.onNext(product);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Product with id " + request.getProductId() + " not found")
                    .asRuntimeException());
        }
    }

    @Override
    public void listProducts(ListProductsRequest request, StreamObserver<Product> responseObserver) {
        int limit = request.getLimit() > 0 ? request.getLimit() : 50;

        List<Product> matches = productsById.values().stream()
                .filter(p -> request.getCategory().isBlank() || p.getCategoriesList().contains(request.getCategory()))
                .filter(p -> !request.getInStockOnly() || p.getInStock())
                .limit(limit)
                .toList();

        for (Product product : matches) {
            responseObserver.onNext(product);
        }
        responseObserver.onCompleted();
    }
}
