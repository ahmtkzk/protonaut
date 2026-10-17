package io.protonaut.web.netty;

import io.protonaut.web.dispatch.ProtonautRestDispatcher;
import org.junit.jupiter.api.Test;

import java.net.ServerSocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ProtonautServerPortConflictTest {

    @Test
    void testGracefulPortConflictHandlingWhenFailFastDisabled() throws Exception {
        // Find an open port and bind it with ServerSocket to simulate conflict
        int occupiedPort;
        try (ServerSocket socket = new ServerSocket(0)) {
            occupiedPort = socket.getLocalPort();
        }

        // Occupy the port
        ServerSocket blocker = new ServerSocket(occupiedPort);
        try {
            ProtonautRestDispatcher mockDispatcher = new ProtonautRestDispatcher(
                    null, null, null, null, null, null, null, null
            );

            ProtonautServer server = new ProtonautServer(
                    "localhost",
                    occupiedPort, // Occupied port!
                    0,
                    false,
                    false, // failFastOnPortConflict = false!
                    mockDispatcher
            );

            // Startup should NOT throw an unhandled exception or kill the application
            assertThatCode(server::start).doesNotThrowAnyException();

            // Grpc channel should be null because port was occupied
            assertThat(server.getGrpcChannel()).isNull();

            server.stop();
        } finally {
            blocker.close();
        }
    }
}
