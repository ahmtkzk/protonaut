package io.protonaut.core.models;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record WorkflowDefinition(
        String id,
        String name,
        String description,
        List<WorkflowStep> steps,
        Map<String, String> variables,
        long createdAt,
        long updatedAt
) {
    public WorkflowDefinition {
        steps = steps != null ? List.copyOf(steps) : List.of();
        variables = variables != null ? Map.copyOf(variables) : Collections.emptyMap();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private List<WorkflowStep> steps = List.of();
        private Map<String, String> variables = Map.of();
        private long createdAt = System.currentTimeMillis();
        private long updatedAt = System.currentTimeMillis();

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder steps(List<WorkflowStep> steps) { this.steps = steps; return this; }
        public Builder variables(Map<String, String> variables) { this.variables = variables; return this; }
        public Builder createdAt(long createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(long updatedAt) { this.updatedAt = updatedAt; return this; }

        public WorkflowDefinition build() {
            return new WorkflowDefinition(id, name, description, steps, variables, createdAt, updatedAt);
        }
    }
}
