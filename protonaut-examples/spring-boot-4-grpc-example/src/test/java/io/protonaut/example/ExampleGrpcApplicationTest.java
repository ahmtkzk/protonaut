package io.protonaut.example;

import io.protonaut.autoconfigure.ProtonautLifecycleManager;
import io.protonaut.core.models.ExecutionRequest;
import io.protonaut.core.models.ExecutionResponse;
import io.protonaut.core.models.ProtoSourceModel;
import io.protonaut.core.models.ServiceDescriptorModel;
import io.protonaut.core.spi.DescriptorRegistry;
import io.protonaut.core.spi.ProtoSourceResolver;
import io.protonaut.grpc.execution.DynamicGrpcExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "grpc.server.port=0", // Use random available port for test
        "protonaut.grpc.port=0",
        "protonaut.rest.port=0"
})
public class ExampleGrpcApplicationTest {

    @Autowired
    private DescriptorRegistry descriptorRegistry;

    @Autowired
    private DynamicGrpcExecutor dynamicGrpcExecutor;

    @Autowired
    private ProtoSourceResolver protoSourceResolver;

    @Autowired
    private ProtonautLifecycleManager lifecycleManager;

    @Test
    void shouldDiscoverAllExampleServicesAndExecuteCalls() {
        assertThat(lifecycleManager.isRunning()).isTrue();

        // 1. Verify Discovery
        List<ServiceDescriptorModel> services = descriptorRegistry.getAllServices();
        assertThat(services).hasSizeGreaterThanOrEqualTo(3);

        List<String> serviceNames = services.stream().map(ServiceDescriptorModel::fullName).toList();
        assertThat(serviceNames).contains(
                "io.protonaut.example.user.UserService",
                "io.protonaut.example.order.OrderService",
                "io.protonaut.example.product.ProductService"
        );

        // 2. Verify Execution of Unary Call (UserService/GetUser)
        ExecutionRequest getUserRequest = new ExecutionRequest(
                "io.protonaut.example.user.UserService",
                "GetUser",
                "{\"id\":\"usr-001\"}",
                Map.of("x-custom-token", "{{uuid}}"),
                5,
                "local"
        );

        ExecutionResponse userResponse = dynamicGrpcExecutor.execute(getUserRequest);
        assertThat(userResponse.success()).isTrue();
        assertThat(userResponse.responseJson()).contains("alice");
        assertThat(userResponse.responseJson()).contains("ROLE_ADMIN");

        // 3. Verify Execution of Server Streaming Call (UserService/ListUsers)
        ExecutionRequest listUsersRequest = new ExecutionRequest(
                "io.protonaut.example.user.UserService",
                "ListUsers",
                "{\"limit\": 10}",
                Map.of(),
                5,
                "local"
        );

        ExecutionResponse streamResponse = dynamicGrpcExecutor.execute(listUsersRequest);
        assertThat(streamResponse.success()).isTrue();
        assertThat(streamResponse.streamingResponses()).hasSizeGreaterThanOrEqualTo(2);

        // 4. Verify Original Proto Source Resolution
        Optional<ProtoSourceModel> userProtoSource = protoSourceResolver.resolveProtoSource("io.protonaut.example.user.UserService");
        assertThat(userProtoSource).isPresent();
        assertThat(userProtoSource.get().content()).contains("service UserService");
        assertThat(userProtoSource.get().reconstructed()).isFalse(); // Exact original discovered on classpath!
    }
}
