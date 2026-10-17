package io.protonaut.codegen;

import io.protonaut.core.models.FieldDescriptorModel;
import io.protonaut.core.models.FieldValueType;
import io.protonaut.core.models.MessageDescriptorModel;
import io.protonaut.core.models.MethodDescriptorModel;
import io.protonaut.core.models.MethodType;
import io.protonaut.core.models.ServiceDescriptorModel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CodeGeneratorRegistryTest {

    private final CodeGeneratorRegistry registry = new CodeGeneratorRegistry();

    private final ServiceDescriptorModel sampleService = new ServiceDescriptorModel(
            "com.example.UserService",
            "UserService",
            "com.example.UserService",
            "com.example",
            List.of(),
            "User management service",
            "user.proto"
    );

    private final MethodDescriptorModel sampleMethod = new MethodDescriptorModel(
            "com.example.UserService/GetUser",
            "GetUser",
            "com.example.UserService/GetUser",
            "UserService",
            "com.example",
            MethodType.UNARY,
            new MessageDescriptorModel(
                    "GetUserRequest", "com.example.GetUserRequest",
                    List.of(FieldDescriptorModel.builder().name("user_id").type(FieldValueType.STRING).build()),
                    List.of(), List.of(), List.of(), null, "user.proto"
            ),
            new MessageDescriptorModel("UserResponse", "com.example.UserResponse", List.of(), List.of(), List.of(), List.of(), null, "user.proto"),
            "Gets user by ID",
            "user.proto"
    );

    @Test
    void testGenerateJava() {
        String code = registry.generate("java", sampleService, sampleMethod, "{\"userId\": \"123\"}", Map.of("x-custom-header", "test-val"), "localhost", 6570);

        assertThat(code).contains("UserServiceGrpc.newBlockingStub");
        assertThat(code).contains("getUser(request)");
        assertThat(code).contains("x-custom-header");
        assertThat(code).contains("ManagedChannelBuilder.forAddress(\"localhost\", 6570)");
    }

    @Test
    void testGenerateGo() {
        String code = registry.generate("go", sampleService, sampleMethod, "{}", Map.of(), "api.company.internal", 6570);

        assertThat(code).contains("pb.NewUserServiceClient(conn)");
        assertThat(code).contains("client.GetUser(ctx, req)");
        assertThat(code).contains("api.company.internal:6570");
    }

    @Test
    void testGenerateTypeScript() {
        String code = registry.generate("typescript", sampleService, sampleMethod, "{\"userId\": 101}", Map.of(), "localhost", 6570);

        assertThat(code).contains("proto.UserServiceClient");
        assertThat(code).contains("client.getUser(");
    }
}
