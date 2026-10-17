package io.protonaut.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.protonaut.example.ExampleGrpcApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ExampleGrpcApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "grpc.server.port=0",
        "protonaut.grpc.port=16572",
        "protonaut.rest.port=18592",
        "protonaut.security.mode=RBAC",
        "protonaut.security.require-authentication=true",
        "protonaut.security.allow-anonymous-local-development=false"
})
public class ProtonautSecurityPolicyIT {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String payload = """
            {
                "serviceId": "io.protonaut.example.user.UserService",
                "methodId": "GetUser",
                "requestJson": "{\\"id\\":\\"usr-001\\"}"
            }
            """;

    @Test
    @DisplayName("Acceptance Criteria L: Anonymous Execution Rejected when require-authentication is true")
    void shouldRejectAnonymousExecution() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18592/protonaut/api/execute"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isIn(401, 403);

        JsonNode err = objectMapper.readTree(response.body());
        assertThat(err.has("error")).isTrue();
    }

    @Test
    @DisplayName("Acceptance Criteria L: Viewer Role Forbidden from Executing Requests")
    void shouldRejectViewerExecution() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18592/protonaut/api/execute"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer viewer-token")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(403);

        JsonNode err = objectMapper.readTree(response.body());
        assertThat(err.get("error").asText()).isEqualTo("FORBIDDEN");
        assertThat(err.get("message").asText()).contains("does not have permission");
    }

    @Test
    @DisplayName("Acceptance Criteria L: Admin Role Authorized to Execute Requests")
    void shouldAllowAdminExecution() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18592/protonaut/api/execute"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer admin-token")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode result = objectMapper.readTree(response.body());
        assertThat(result.get("success").asBoolean()).isTrue();
        assertThat(result.get("responseJson").asText()).contains("alice");
    }
}
