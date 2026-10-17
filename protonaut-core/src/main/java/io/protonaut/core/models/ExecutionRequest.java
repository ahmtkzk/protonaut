package io.protonaut.core.models;

import java.util.Collections;
import java.util.Map;

public record ExecutionRequest(
        String serviceId,
        String methodId,
        String requestJson,
        Map<String, String> metadata,
        Integer timeoutSeconds,
        String environment
) {
    public ExecutionRequest {
        metadata = metadata != null ? Map.copyOf(metadata) : Collections.emptyMap();
        timeoutSeconds = (timeoutSeconds != null && timeoutSeconds > 0) ? timeoutSeconds : 30;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String serviceId;
        private String methodId;
        private String requestJson;
        private Map<String, String> metadata;
        private Integer timeoutSeconds;
        private String environment;

        public Builder serviceId(String serviceId) { this.serviceId = serviceId; return this; }
        public Builder methodId(String methodId) { this.methodId = methodId; return this; }
        public Builder requestJson(String requestJson) { this.requestJson = requestJson; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }
        public Builder timeoutSeconds(Integer timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; return this; }
        public Builder environment(String environment) { this.environment = environment; return this; }

        public ExecutionRequest build() {
            return new ExecutionRequest(serviceId, methodId, requestJson, metadata, timeoutSeconds, environment);
        }
    }
}
