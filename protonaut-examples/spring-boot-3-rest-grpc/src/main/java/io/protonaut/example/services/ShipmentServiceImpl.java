package io.protonaut.example.services;

import io.grpc.stub.StreamObserver;
import io.protonaut.example.warehouse.ShipmentServiceGrpc;
import io.protonaut.example.warehouse.TrackShipmentRequest;
import io.protonaut.example.warehouse.TrackShipmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShipmentServiceImpl extends ShipmentServiceGrpc.ShipmentServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(ShipmentServiceImpl.class);

    @Override
    public void trackShipment(TrackShipmentRequest request, StreamObserver<TrackShipmentResponse> responseObserver) {
        String trackingNumber = request.getTrackingNumber();
        log.info("[gRPC:ShipmentService] Tracking shipment: '{}'", trackingNumber);

        TrackShipmentResponse response = TrackShipmentResponse.newBuilder()
                .setTrackingNumber(trackingNumber)
                .setCarrier("DHL Express")
                .setStatus("IN_TRANSIT")
                .setEstimatedDelivery("Tomorrow, 14:00")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
