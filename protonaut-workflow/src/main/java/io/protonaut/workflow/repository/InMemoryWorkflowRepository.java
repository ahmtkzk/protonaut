package io.protonaut.workflow.repository;

import io.protonaut.core.models.AssertionRule;
import io.protonaut.core.models.FailureStrategy;
import io.protonaut.core.models.WorkflowDefinition;
import io.protonaut.core.models.WorkflowStep;
import io.protonaut.core.spi.WorkflowRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryWorkflowRepository implements WorkflowRepository {
    private final Map<String, WorkflowDefinition> store = new ConcurrentHashMap<>();

    public InMemoryWorkflowRepository() {
        initDefaultSampleWorkflow();
    }

    private void initDefaultSampleWorkflow() {
        WorkflowDefinition sampleFlow = WorkflowDefinition.builder()
                .id("sample-user-order-flow")
                .name("User & Order Verification Flow")
                .description("Demonstrates chained gRPC calls: GetUser -> Assert Name -> CreateOrder -> Verify Order")
                .variables(Map.of("defaultUserId", "usr-101", "storeName", "Protonaut Market"))
                .steps(List.of(
                        WorkflowStep.builder()
                                .id("step1_getUser")
                                .name("1. Get User Profile")
                                .serviceId("UserService")
                                .methodId("GetUser")
                                .requestJson("{\"userId\": \"{{defaultUserId}}\"}")
                                .assertions(List.of(
                                        new AssertionRule("response.name", "==", "Selam", "User name must be Selam"),
                                        new AssertionRule("response.email", "contains", "@", "Email must be valid")
                                ))
                                .nodePosition(Map.of("x", 100, "y", 100))
                                .build(),
                        WorkflowStep.builder()
                                .id("step2_createOrder")
                                .name("2. Create Customer Order")
                                .serviceId("OrderService")
                                .methodId("CreateOrder")
                                .requestJson("{\"userId\": \"{{steps.step1_getUser.response.userId}}\", \"items\": [{\"productId\": \"prod-99\", \"quantity\": 2}], \"totalAmount\": 150.00}")
                                .assertions(List.of(
                                        new AssertionRule("response.orderId", "exists", "", "Order ID must be generated")
                                ))
                                .nodePosition(Map.of("x", 100, "y", 250))
                                .build(),
                        WorkflowStep.builder()
                                .id("step3_verifyUser")
                                .name("3. Re-verify User Orders")
                                .serviceId("UserService")
                                .methodId("GetUser")
                                .requestJson("{\"userId\": \"{{steps.step1_getUser.response.userId}}\"}")
                                .assertions(List.of(
                                        new AssertionRule("response.userId", "==", "usr-101", "User ID matches initial call")
                                ))
                                .nodePosition(Map.of("x", 100, "y", 400))
                                .build()
                ))
                .build();

        store.put(sampleFlow.id(), sampleFlow);
    }

    @Override
    public List<WorkflowDefinition> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public Optional<WorkflowDefinition> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public WorkflowDefinition save(WorkflowDefinition workflow) {
        if (workflow == null) throw new IllegalArgumentException("Workflow cannot be null");
        String id = workflow.id() != null && !workflow.id().isBlank() ? workflow.id() : UUID.randomUUID().toString();
        WorkflowDefinition updated = WorkflowDefinition.builder()
                .id(id)
                .name(workflow.name())
                .description(workflow.description())
                .steps(workflow.steps())
                .variables(workflow.variables())
                .createdAt(workflow.createdAt() > 0 ? workflow.createdAt() : System.currentTimeMillis())
                .updatedAt(System.currentTimeMillis())
                .build();
        store.put(id, updated);
        return updated;
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null) return false;
        return store.remove(id) != null;
    }
}
