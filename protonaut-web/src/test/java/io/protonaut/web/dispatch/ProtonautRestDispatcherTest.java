package io.protonaut.web.dispatch;

import io.protonaut.codegen.CodeGeneratorRegistry;
import io.protonaut.core.models.*;
import io.protonaut.core.registry.DefaultDescriptorRegistry;
import io.protonaut.core.spi.DescriptorRegistry;
import io.protonaut.core.spi.GrpcExecutionProvider;
import io.protonaut.core.spi.ProtoSourceResolver;
import io.protonaut.core.spi.WorkflowEngine;
import io.protonaut.core.spi.WorkflowRepository;
import io.protonaut.security.ProtonautSecurityManager;
import io.protonaut.security.SecurityConfigModel;
import io.protonaut.workflow.engine.WorkflowEngineImpl;
import io.protonaut.workflow.repository.InMemoryWorkflowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProtonautRestDispatcherTest {

    private ProtonautRestDispatcher dispatcher;
    private DescriptorRegistry descriptorRegistry;

    @BeforeEach
    void setup() {
        descriptorRegistry = new DefaultDescriptorRegistry();
        ServiceDescriptorModel sampleService = new ServiceDescriptorModel(
                "com.example.UserService",
                "UserService",
                "com.example.UserService",
                "com.example",
                List.of(
                        new MethodDescriptorModel(
                                "com.example.UserService/GetUser",
                                "GetUser",
                                "com.example.UserService/GetUser",
                                "UserService",
                                "com.example",
                                MethodType.UNARY,
                                new MessageDescriptorModel("GetUserRequest", "com.example.GetUserRequest", List.of(), List.of(), List.of(), List.of(), null, "user.proto"),
                                new MessageDescriptorModel("UserResponse", "com.example.UserResponse", List.of(), List.of(), List.of(), List.of(), null, "user.proto"),
                                "Gets user profile",
                                "user.proto"
                        )
                ),
                "User Service",
                "user.proto"
        );
        descriptorRegistry.registerService(sampleService);

        GrpcExecutionProvider mockExec = req -> ExecutionResponse.success(
                "req-100", req.serviceId(), req.methodId(),
                "{\"name\": \"Selam\"}", 10, 20, Map.of(), Map.of()
        );

        ProtoSourceResolver mockProto = new ProtoSourceResolver() {
            @Override
            public Optional<ProtoSourceModel> resolveProtoSource(String serviceId) {
                return Optional.of(ProtoSourceModel.original("user.proto", "com.example", "syntax = \"proto3\";", "proto3"));
            }

            @Override
            public List<ProtoSourceModel> resolveAllProtoSources() {
                return List.of(ProtoSourceModel.original("user.proto", "com.example", "syntax = \"proto3\";", "proto3"));
            }
        };

        WorkflowRepository workflowRepo = new InMemoryWorkflowRepository();
        WorkflowEngine workflowEngine = new WorkflowEngineImpl(mockExec);
        ProtonautSecurityManager securityManager = new ProtonautSecurityManager(SecurityConfigModel.localDevelopment());

        EnvironmentInfo envInfo = new EnvironmentInfo(
                "Test Project", "Engineering", "test", "1.0.0", "Test Description",
                6570, 8590, true, "localhost", "UP", Map.of()
        );

        dispatcher = new ProtonautRestDispatcher(
                descriptorRegistry,
                mockExec,
                mockProto,
                workflowEngine,
                workflowRepo,
                new CodeGeneratorRegistry(),
                securityManager,
                envInfo
        );
    }

    @Test
    void testGetInfo() {
        var resp = dispatcher.dispatch("GET", "/protonaut/api/info", Map.of(), null);
        assertThat(resp.statusCode()).isEqualTo(200);
        assertThat(new String(resp.body())).contains("Test Project");
    }

    @Test
    void testGetServices() {
        var resp = dispatcher.dispatch("GET", "/protonaut/api/services", Map.of(), null);
        assertThat(resp.statusCode()).isEqualTo(200);
        assertThat(new String(resp.body())).contains("UserService");
    }

    @Test
    void testExecuteMethod() {
        String jsonPayload = """
                {
                  "serviceId": "UserService",
                  "methodId": "GetUser",
                  "requestJson": "{\\"userId\\": \\"101\\"}"
                }
                """;

        var resp = dispatcher.dispatch("POST", "/protonaut/api/execute", Map.of(), jsonPayload.getBytes());
        assertThat(resp.statusCode()).isEqualTo(200);
        assertThat(new String(resp.body())).contains("Selam");
        assertThat(new String(resp.body())).contains("req-100");
    }

    @Test
    void testGetPublicConfig() {
        var resp = dispatcher.dispatch("GET", "/protonaut/api/config/public", Map.of(), null);
        assertThat(resp.statusCode()).isEqualTo(200);
        assertThat(new String(resp.body())).contains("executionEnabled");
    }

    @Test
    void testRedirectToTrailingSlash() {
        var resp = dispatcher.dispatch("GET", "/protonaut", Map.of(), null);
        assertThat(resp.statusCode()).isEqualTo(302);
        assertThat(resp.headers()).containsEntry("Location", "./protonaut/");

        var rootResp = dispatcher.dispatch("GET", "/", Map.of(), null);
        assertThat(rootResp.statusCode()).isEqualTo(302);
        assertThat(rootResp.headers()).containsEntry("Location", "./protonaut/");
    }
}
