package io.protonaut.core.models;

import java.util.Collections;
import java.util.List;

public record WorkflowStepResult(
        String stepId,
        String stepName,
        WorkflowStatus status,
        String requestJson,
        String responseJson,
        GrpcStatusModel grpcStatus,
        long durationMs,
        boolean assertionsPassed,
        List<String> assertionErrors,
        String errorMessage,
        int attempts
) {
    public WorkflowStepResult {
        assertionErrors = assertionErrors != null ? List.copyOf(assertionErrors) : Collections.emptyList();
    }

    public static WorkflowStepResult success(
            String stepId,
            String stepName,
            String requestJson,
            String responseJson,
            long durationMs,
            int attempts
    ) {
        return new WorkflowStepResult(
                stepId, stepName, WorkflowStatus.SUCCESS,
                requestJson, responseJson, GrpcStatusModel.OK,
                durationMs, true, List.of(), null, attempts
        );
    }

    public static WorkflowStepResult failure(
            String stepId,
            String stepName,
            String requestJson,
            String responseJson,
            GrpcStatusModel grpcStatus,
            long durationMs,
            List<String> assertionErrors,
            String errorMessage,
            int attempts
    ) {
        return new WorkflowStepResult(
                stepId, stepName, WorkflowStatus.FAILED,
                requestJson, responseJson, grpcStatus,
                durationMs, false, assertionErrors, errorMessage, attempts
        );
    }

    public static WorkflowStepResult skipped(String stepId, String stepName, String reason) {
        return new WorkflowStepResult(
                stepId, stepName, WorkflowStatus.SKIPPED,
                null, null, null, 0, true, List.of(), reason, 0
        );
    }
}
