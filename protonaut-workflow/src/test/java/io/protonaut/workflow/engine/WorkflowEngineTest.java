package io.protonaut.workflow.engine;

import io.protonaut.core.models.*;
import io.protonaut.core.spi.GrpcExecutionProvider;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowEngineTest {

    @Test
    void testSequentialWorkflowWithVariablesAndAssertions() {
        GrpcExecutionProvider mockProvider = request -> {
            if ("UserService".equals(request.serviceId()) && "GetUser".equals(request.methodId())) {
                return ExecutionResponse.success(
                        "req-1", "UserService", "GetUser",
                        "{\"userId\": \"usr-101\", \"name\": \"Selam\"}",
                        15, 50, Map.of(), Map.of()
                );
            } else if ("OrderService".equals(request.serviceId()) && "CreateOrder".equals(request.methodId())) {
                // Verify that the request body had the interpolated user ID!
                assertThat(request.requestJson()).contains("\"customerId\": \"usr-101\"");
                return ExecutionResponse.success(
                        "req-2", "OrderService", "CreateOrder",
                        "{\"orderId\": \"ord-9001\", \"status\": \"CREATED\"}",
                        20, 45, Map.of(), Map.of()
                );
            }
            return ExecutionResponse.failure("req-x", request.serviceId(), request.methodId(),
                    new GrpcStatusModel(12, "UNIMPLEMENTED", "Unknown method", null), 0, "Unknown", Map.of(), Map.of());
        };

        WorkflowEngineImpl engine = new WorkflowEngineImpl(mockProvider);

        WorkflowDefinition workflow = WorkflowDefinition.builder()
                .id("test-flow")
                .name("Chained Flow")
                .steps(List.of(
                        WorkflowStep.builder()
                                .id("step1")
                                .name("Get User")
                                .serviceId("UserService")
                                .methodId("GetUser")
                                .requestJson("{\"userId\": \"usr-101\"}")
                                .assertions(List.of(new AssertionRule("response.name", "==", "Selam", null)))
                                .build(),
                        WorkflowStep.builder()
                                .id("step2")
                                .name("Create Order")
                                .serviceId("OrderService")
                                .methodId("CreateOrder")
                                .requestJson("{\"customerId\": \"{{steps.step1.response.userId}}\"}")
                                .assertions(List.of(new AssertionRule("response.status", "==", "CREATED", null)))
                                .build()
                ))
                .build();

        WorkflowExecutionResult result = engine.execute(workflow);

        assertThat(result.status()).isEqualTo(WorkflowStatus.SUCCESS);
        assertThat(result.stepResults()).hasSize(2);
        assertThat(result.stepResults().get(0).assertionsPassed()).isTrue();
        assertThat(result.stepResults().get(1).assertionsPassed()).isTrue();
    }
}
