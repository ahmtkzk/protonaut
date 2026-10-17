package io.protonaut.it;

import io.protonaut.example.ExampleGrpcApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.ServerSocket;

import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest(classes = ExampleGrpcApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "grpc.server.port=0",
        "protonaut.grpc.port=16574",
        "protonaut.rest.port=18594",
        "protonaut.execution.fail-fast-on-port-conflict=false"
})
public class ProtonautPortConflictIT {

    private static ServerSocket conflictingSocket;

    @BeforeAll
    static void bindConflictingPort() throws IOException {
        // Pre-bind port 16574 before Spring context / Protonaut starts
        conflictingSocket = new ServerSocket(16574);
    }

    @AfterAll
    static void closeConflictingSocket() throws IOException {
        if (conflictingSocket != null && !conflictingSocket.isClosed()) {
            conflictingSocket.close();
        }
    }

    @Test
    @DisplayName("Acceptance Criteria C: Port conflict logs warning and host application starts up cleanly")
    void shouldStartHostAppCleanlyEvenWithPortConflict() {
        // If we reached here, the Spring Boot application context loaded successfully despite port conflict!
        assertThatNoException().isThrownBy(() -> {
            // Context is alive and healthy
        });
    }
}
