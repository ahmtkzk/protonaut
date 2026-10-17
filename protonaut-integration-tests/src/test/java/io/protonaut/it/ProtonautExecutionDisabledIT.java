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
        "protonaut.grpc.port=16573",
        "protonaut.rest.port=18593",
        "protonaut.execution.enabled=false",
        "protonaut.workflow.execution-enabled=false"
})
public class ProtonautExecutionDisabledIT {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Acceptance Criteria M: Global Execution Switch Disabled Blocks All Request Execution")
    void shouldRejectExecutionWhenGloballyDisabled() throws Exception {
        String payload = """
                {
                    "serviceId": "io.protonaut.example.user.UserService",
                    "methodId": "GetUser",
                    "requestJson": "{\\"id\\":\\"usr-001\\"}"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18593/protonaut/api/execute"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer admin-token")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(403);

        JsonNode err = objectMapper.readTree(response.body());
        assertThat(err.get("error").asText()).isEqualTo("FORBIDDEN");
        assertThat(err.get("message").asText()).contains("Request execution is disabled");
    }
}
