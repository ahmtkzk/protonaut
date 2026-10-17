package io.protonaut.core.models;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record WorkflowStep(
        String id,
        String name,
        String serviceId,
        String methodId,
        String requestJson,
        Map<String, String> metadata,
        Integer timeoutSeconds,
        String condition,
        List<AssertionRule> assertions,
        FailureStrategy failureStrategy,
        int retryCount,
        long retryBackoffMs,
        Map<String, Object> nodePosition
) {
    public WorkflowStep {
        metadata = metadata != null ? Map.copyOf(metadata) : Collections.emptyMap();
        assertions = assertions != null ? List.copyOf(assertions) : Collections.emptyList();
        failureStrategy = failureStrategy != null ? failureStrategy : FailureStrategy.STOP_ON_FAILURE;
        timeoutSeconds = (timeoutSeconds != null && timeoutSeconds > 0) ? timeoutSeconds : 30;
        nodePosition = nodePosition != null ? Map.copyOf(nodePosition) : Collections.emptyMap();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String serviceId;
        private String methodId;
        private String requestJson;
        private Map<String, String> metadata;
        private Integer timeoutSeconds;
        private String condition;
        private List<AssertionRule> assertions;
        private FailureStrategy failureStrategy = FailureStrategy.STOP_ON_FAILURE;
        private int retryCount = 0;
        private long retryBackoffMs = 500;
        private Map<String, Object> nodePosition;

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder serviceId(String serviceId) { this.serviceId = serviceId; return this; }
        public Builder methodId(String methodId) { this.methodId = methodId; return this; }
        public Builder requestJson(String requestJson) { this.requestJson = requestJson; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }
        public Builder timeoutSeconds(Integer timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; return this; }
        public Builder condition(String condition) { this.condition = condition; return this; }
        public Builder assertions(List<AssertionRule> assertions) { this.assertions = assertions; return this; }
        public Builder failureStrategy(FailureStrategy failureStrategy) { this.failureStrategy = failureStrategy; return this; }
        public Builder retryCount(int retryCount) { this.retryCount = retryCount; return this; }
        public Builder retryBackoffMs(long retryBackoffMs) { this.retryBackoffMs = retryBackoffMs; return this; }
        public Builder nodePosition(Map<String, Object> nodePosition) { this.nodePosition = nodePosition; return this; }

        public WorkflowStep build() {
            return new WorkflowStep(id, name, serviceId, methodId, requestJson, metadata,
                    timeoutSeconds, condition, assertions, failureStrategy, retryCount, retryBackoffMs, nodePosition);
        }
    }
}
