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
        "protonaut.grpc.port=16570",
        "protonaut.rest.port=18590",
        "protonaut.rest.enabled=true",
        "protonaut.security.mode=ANONYMOUS_DEV",
        "protonaut.security.allow-anonymous-local-development=true",
        "protonaut.security.require-authentication=false"
})
public class ProtonautEndToEndIT {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Acceptance Criteria A: Dual Port Accessibility - Both Port 16570 and Port 18590 serve UI and APIs")
    void shouldServeUiAndApiOnBothPorts() throws Exception {
        // Port 16570 (grpc.port browser entry point)
        HttpRequest requestPort6570 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:16570/protonaut"))
                .GET()
                .build();
        HttpResponse<String> responsePort6570 = httpClient.send(requestPort6570, HttpResponse.BodyHandlers.ofString());
        assertThat(responsePort6570.statusCode()).isEqualTo(200);
        assertThat(responsePort6570.body()).contains("Protonaut");

        // Port 18590 (rest.port dedicated REST entry point)
        HttpRequest requestPort8590 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18590/protonaut"))
                .GET()
                .build();
        HttpResponse<String> responsePort8590 = httpClient.send(requestPort8590, HttpResponse.BodyHandlers.ofString());
        assertThat(responsePort8590.statusCode()).isEqualTo(200);
        assertThat(responsePort8590.body()).contains("Protonaut");
    }

    @Test
    @DisplayName("Acceptance Criteria D: Automatic Service & Method Discovery via REST Endpoint")
    void shouldDiscoverAllServicesViaApi() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18590/protonaut/api/services"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode services = objectMapper.readTree(response.body());
        assertThat(services.isArray()).isTrue();
        assertThat(services.size()).isGreaterThanOrEqualTo(3);

        boolean foundUser = false;
        boolean foundOrder = false;
        boolean foundProduct = false;

        for (JsonNode svc : services) {
            String fullName = svc.get("fullName").asText();
            if (fullName.equals("io.protonaut.example.user.UserService")) foundUser = true;
            if (fullName.equals("io.protonaut.example.order.OrderService")) foundOrder = true;
            if (fullName.equals("io.protonaut.example.product.ProductService")) foundProduct = true;
        }

        assertThat(foundUser).isTrue();
        assertThat(foundOrder).isTrue();
        assertThat(foundProduct).isTrue();
    }

    @Test
    @DisplayName("Acceptance Criteria E: Original Protobuf Source Resolution")
    void shouldResolveOriginalProtoSource() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18590/protonaut/api/proto?service=io.protonaut.example.user.UserService"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode protoSource = objectMapper.readTree(response.body());
        assertThat(protoSource.get("content").asText()).contains("service UserService");
        assertThat(protoSource.get("reconstructed").asBoolean()).isFalse();
    }

    @Test
    @DisplayName("Acceptance Criteria F: Interactive gRPC Execution & Protobuf JSON Mapping")
    void shouldExecuteGrpcMethodViaRestApi() throws Exception {
        String payload = """
                {
                    "serviceId": "io.protonaut.example.user.UserService",
                    "methodId": "GetUser",
                    "requestJson": "{\\"id\\":\\"usr-001\\"}",
                    "metadata": {
                        "x-request-id": "{{uuid}}"
                    }
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18590/protonaut/api/execute"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode execResult = objectMapper.readTree(response.body());
        assertThat(execResult.get("success").asBoolean()).isTrue();
        assertThat(execResult.get("responseJson").asText()).contains("alice");
        assertThat(execResult.get("responseJson").asText()).contains("ROLE_ADMIN");
        assertThat(execResult.get("durationMs").asLong()).isGreaterThanOrEqualTo(0);
        assertThat(execResult.get("responseSizeBytes").asLong()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Acceptance Criteria G: Type-Aware Mock/Dummy Data Generation")
    void shouldGenerateDummyDataForMethod() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18590/protonaut/api/dummy-data?method=io.protonaut.example.user.UserService/CreateUser"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode dummyData = objectMapper.readTree(response.body());
        assertThat(dummyData.has("username")).isTrue();
        assertThat(dummyData.has("email")).isTrue();
        assertThat(dummyData.has("role")).isTrue();
    }

    @Test
    @DisplayName("Acceptance Criteria H: Multi-Language Client Code Generation")
    void shouldGenerateClientCode() throws Exception {
        HttpRequest requestJava = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18590/protonaut/api/codegen?method=io.protonaut.example.user.UserService/GetUser&language=java"))
                .GET()
                .build();
        HttpResponse<String> responseJava = httpClient.send(requestJava, HttpResponse.BodyHandlers.ofString());
        assertThat(responseJava.statusCode()).isEqualTo(200);
        assertThat(responseJava.body()).contains("ManagedChannel");
        assertThat(responseJava.body()).contains("UserServiceGrpc");

        HttpRequest requestGo = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18590/protonaut/api/codegen?method=io.protonaut.example.user.UserService/GetUser&language=go"))
                .GET()
                .build();
        HttpResponse<String> responseGo = httpClient.send(requestGo, HttpResponse.BodyHandlers.ofString());
        assertThat(responseGo.statusCode()).isEqualTo(200);
        assertThat(responseGo.body()).contains("grpc.NewClient");
    }

    @Test
    @DisplayName("Acceptance Criteria J & K: Sequential Workflow Execution with Assertions and Dynamic Variables")
    void shouldExecuteWorkflowSuccessfully() throws Exception {
        String workflowJson = """
                {
                    "id": "test-user-order-flow",
                    "name": "User Order Flow Test",
                    "description": "Fetches a user and creates an order",
                    "initialVariables": {
                        "userId": "usr-001"
                    },
                    "steps": [
                        {
                            "id": "step1",
                            "name": "Get User Details",
                            "serviceId": "io.protonaut.example.user.UserService",
                            "methodId": "GetUser",
                            "requestJson": "{\\"id\\":\\"{{vars.userId}}\\"}",
                            "metadata": {},
                            "assertions": [
                                {
                                    "target": "response.username",
                                    "operator": "==",
                                    "expectedValue": "alice"
                                }
                            ]
                        },
                        {
                            "id": "step2",
                            "name": "Create Order for User",
                            "serviceId": "io.protonaut.example.order.OrderService",
                            "methodId": "CreateOrder",
                            "requestJson": "{\\"userId\\":\\"{{steps.step1.response.id}}\\",\\"items\\":[{\\"productId\\":\\"prod-001\\",\\"quantity\\":1,\\"unitPrice\\":499.0}]}",
                            "metadata": {},
                            "assertions": [
                                {
                                    "target": "response.status",
                                    "operator": "==",
                                    "expectedValue": "ORDER_CONFIRMED"
                                }
                            ]
                        }
                    ]
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:18590/protonaut/api/workflow/execute"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(workflowJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode wfResult = objectMapper.readTree(response.body());
        assertThat(wfResult.get("status").asText()).isEqualTo("SUCCESS");
        assertThat(wfResult.get("stepResults").size()).isEqualTo(2);
        assertThat(wfResult.get("stepResults").get(0).get("status").asText()).isEqualTo("SUCCESS");
        assertThat(wfResult.get("stepResults").get(1).get("status").asText()).isEqualTo("SUCCESS");
    }
}
