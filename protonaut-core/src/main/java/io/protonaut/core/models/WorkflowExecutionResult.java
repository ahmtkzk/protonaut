package io.protonaut.core.models;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record WorkflowExecutionResult(
        String executionId,
        String workflowId,
        String workflowName,
        WorkflowStatus status,
        long durationMs,
        List<WorkflowStepResult> stepResults,
        Map<String, Object> contextVariables,
        long startedAt,
        long completedAt,
        String errorMessage
) {
    public WorkflowExecutionResult {
        stepResults = stepResults != null ? List.copyOf(stepResults) : Collections.emptyList();
        contextVariables = contextVariables != null ? Map.copyOf(contextVariables) : Collections.emptyMap();
    }
}
