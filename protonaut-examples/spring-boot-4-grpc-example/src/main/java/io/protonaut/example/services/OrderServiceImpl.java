package io.protonaut.example.services;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.protonaut.example.order.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final Map<String, Order> ordersById = new ConcurrentHashMap<>();

    public OrderServiceImpl() {
        seedSampleOrders();
    }

    private void seedSampleOrders() {
        Order order1 = Order.newBuilder()
                .setId("ord-1001")
                .setOrderNumber("ORD-2026-0001")
                .setUserId("usr-001")
                .addItems(OrderItem.newBuilder()
                        .setProductId("prod-001")
                        .setProductName("High-Performance gRPC Gateway")
                        .setQuantity(2)
                        .setUnitPrice(499.00)
                        .setTotalPrice(998.00)
                        .build())
                .addItems(OrderItem.newBuilder()
                        .setProductId("prod-002")
                        .setProductName("Cloud Observability Agent")
                        .setQuantity(1)
                        .setUnitPrice(199.00)
                        .setTotalPrice(199.00)
                        .build())
                .setTotalAmount(1197.00)
                .setCurrency("USD")
                .setStatus(OrderStatus.ORDER_CONFIRMED)
                .setShippingAddress("123 Cloud Way, San Francisco, CA 94105")
                .setCreatedAt(Instant.now().minusSeconds(86400 * 5).toEpochMilli())
                .build();

        ordersById.put(order1.getId(), order1);
    }

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<Order> responseObserver) {
        if (request.getUserId().isBlank()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("user_id is required")
                    .asRuntimeException());
            return;
        }

        if (request.getItemsList().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Order must contain at least one item")
                    .asRuntimeException());
            return;
        }

        String orderId = "ord-" + UUID.randomUUID().toString().substring(0, 8);
        String orderNumber = "ORD-" + System.currentTimeMillis() % 100000;

        double total = 0.0;
        List<OrderItem> calculatedItems = new ArrayList<>();
        for (OrderItem item : request.getItemsList()) {
            double lineTotal = item.getQuantity() * item.getUnitPrice();
            total += lineTotal;
            calculatedItems.add(item.toBuilder().setTotalPrice(lineTotal).build());
        }

        String currency = request.getCurrency().isBlank() ? "USD" : request.getCurrency();

        Order order = Order.newBuilder()
                .setId(orderId)
                .setOrderNumber(orderNumber)
                .setUserId(request.getUserId())
                .addAllItems(calculatedItems)
                .setTotalAmount(total)
                .setCurrency(currency)
                .setStatus(OrderStatus.ORDER_CONFIRMED)
                .setShippingAddress(request.getShippingAddress())
                .setCreatedAt(Instant.now().toEpochMilli())
                .build();

        ordersById.put(orderId, order);

        responseObserver.onNext(order);
        responseObserver.onCompleted();
    }

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<Order> responseObserver) {
        Order order = ordersById.get(request.getOrderId());
        if (order != null) {
            responseObserver.onNext(order);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Order with id " + request.getOrderId() + " not found")
                    .asRuntimeException());
        }
    }

    @Override
    public void cancelOrder(CancelOrderRequest request, StreamObserver<Order> responseObserver) {
        Order existing = ordersById.get(request.getOrderId());
        if (existing == null) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Order with id " + request.getOrderId() + " not found")
                    .asRuntimeException());
            return;
        }

        if (existing.getStatus() == OrderStatus.ORDER_SHIPPED || existing.getStatus() == OrderStatus.ORDER_DELIVERED) {
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription("Cannot cancel order in status: " + existing.getStatus())
                    .asRuntimeException());
            return;
        }

        Order cancelled = existing.toBuilder()
                .setStatus(OrderStatus.ORDER_CANCELLED)
                .build();

        ordersById.put(cancelled.getId(), cancelled);

        responseObserver.onNext(cancelled);
        responseObserver.onCompleted();
    }
}
