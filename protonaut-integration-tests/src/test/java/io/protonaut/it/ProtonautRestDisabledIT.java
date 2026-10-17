package io.protonaut.it;

import io.protonaut.example.ExampleGrpcApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ExampleGrpcApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "grpc.server.port=0",
        "protonaut.grpc.port=16571",
        "protonaut.rest.port=18591",
        "protonaut.rest.enabled=false"
})
public class ProtonautRestDisabledIT {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    @Test
    @DisplayName("Acceptance Criteria B: When protonaut.rest.enabled=false, Port 18591 is closed while Port 16571 serves UI & APIs")
    void shouldCloseRestPortWhileKeepingGrpcPortOpen() throws Exception {
        // Port 16571 remains completely open and serves UI & API
        HttpRequest grpcPortReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:16571/protonaut"))
                .GET()
                .build();
        HttpResponse<String> grpcPortResp = httpClient.send(grpcPortReq, HttpResponse.BodyHandlers.ofString());
        assertThat(grpcPortResp.statusCode()).isEqualTo(200);

        // Port 18591 should be closed (connection refused)
        HttpRequest restPortReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18591/protonaut"))
                .GET()
                .build();

        assertThatThrownBy(() -> httpClient.send(restPortReq, HttpResponse.BodyHandlers.ofString()))
                .isInstanceOf(IOException.class);
    }
}
