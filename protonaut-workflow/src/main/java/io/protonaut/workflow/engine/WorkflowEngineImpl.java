package io.protonaut.workflow.engine;

import io.protonaut.core.models.*;
import io.protonaut.core.spi.GrpcExecutionProvider;
import io.protonaut.core.spi.WorkflowEngine;
import io.protonaut.workflow.evaluator.SafeExpressionEvaluator;
import io.protonaut.workflow.interpolation.VariableInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WorkflowEngineImpl implements WorkflowEngine {
    private static final Logger log = LoggerFactory.getLogger(WorkflowEngineImpl.class);

    private final GrpcExecutionProvider executionProvider;

    public WorkflowEngineImpl(GrpcExecutionProvider executionProvider) {
        this.executionProvider = Objects.requireNonNull(executionProvider, "GrpcExecutionProvider must not be null");
    }

    @Override
    public WorkflowExecutionResult execute(WorkflowDefinition workflow) {
        String executionId = UUID.randomUUID().toString();
        long startedAt = System.currentTimeMillis();
        long startNano = System.nanoTime();

        log.info("Starting execution of workflow '{}' [{}] with {} steps",
                workflow.name(), workflow.id(), workflow.steps().size());

        Map<String, WorkflowStepResult> stepResultsById = new LinkedHashMap<>();
        List<WorkflowStepResult> stepResults = new ArrayList<>();
        Map<String, String> variables = new HashMap<>(workflow.variables());
        WorkflowStatus overallStatus = WorkflowStatus.SUCCESS;
        String executionError = null;

        for (int i = 0; i < workflow.steps().size(); i++) {
            WorkflowStep step = workflow.steps().get(i);
            log.debug("Executing step {}/{}: {} ({})", i + 1, workflow.steps().size(), step.name(), step.id());

            // 1. Evaluate Step Condition if present
            if (step.condition() != null && !step.condition().isBlank()) {
                String previousJson = !stepResults.isEmpty()
                        ? stepResults.get(stepResults.size() - 1).responseJson()
                        : "{}";
                boolean shouldRun = SafeExpressionEvaluator.evaluateCondition(step.condition(), previousJson);
                if (!shouldRun) {
                    log.info("Step '{}' skipped due to condition: {}", step.name(), step.condition());
                    WorkflowStepResult skippedResult = WorkflowStepResult.skipped(step.id(), step.name(), "Condition evaluated to false: " + step.condition());
                    stepResults.add(skippedResult);
                    stepResultsById.put(step.id(), skippedResult);
                    continue;
                }
            }

            // 2. Interpolate dynamic variables in request payload and metadata
            String interpolatedRequest = VariableInterpolator.interpolate(step.requestJson(), stepResultsById, variables);
            Map<String, String> interpolatedMetadata = new HashMap<>();
            for (Map.Entry<String, String> entry : step.metadata().entrySet()) {
                interpolatedMetadata.put(entry.getKey(), VariableInterpolator.interpolate(entry.getValue(), stepResultsById, variables));
            }

            // 3. Execute step with retry logic
            int maxAttempts = Math.max(1, step.retryCount() + 1);
            int attempts = 0;
            ExecutionResponse response = null;

            while (attempts < maxAttempts) {
                attempts++;
                ExecutionRequest execRequest = ExecutionRequest.builder()
                        .serviceId(step.serviceId())
                        .methodId(step.methodId())
                        .requestJson(interpolatedRequest)
                        .metadata(interpolatedMetadata)
                        .timeoutSeconds(step.timeoutSeconds())
                        .build();

                response = executionProvider.execute(execRequest);
                if (response.success()) {
                    break;
                }

                if (attempts < maxAttempts && step.retryBackoffMs() > 0) {
                    try {
                        Thread.sleep(step.retryBackoffMs());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            // 4. Evaluate Assertions
            List<String> assertionErrors = new ArrayList<>();
            if (response != null && response.success()) {
                for (AssertionRule rule : step.assertions()) {
                    SafeExpressionEvaluator.EvaluationResult evalResult =
                            SafeExpressionEvaluator.evaluateAssertion(response.responseJson(), rule);
                    if (!evalResult.passed()) {
                        assertionErrors.add(evalResult.errorMessage());
                    }
                }
            }

            // 5. Build Step Result
            boolean stepSuccess = response != null && response.success() && assertionErrors.isEmpty();
            WorkflowStepResult stepResult;

            if (stepSuccess) {
                stepResult = WorkflowStepResult.success(
                        step.id(),
                        step.name(),
                        interpolatedRequest,
                        response.responseJson(),
                        response.durationMs(),
                        attempts
                );
            } else {
                String errorMsg = (response != null && response.errorMessage() != null)
                        ? response.errorMessage()
                        : (!assertionErrors.isEmpty() ? String.join("; ", assertionErrors) : "Execution failed");

                GrpcStatusModel statusModel = response != null ? response.grpcStatus() : new GrpcStatusModel(13, "INTERNAL", "No response", null);
                long duration = response != null ? response.durationMs() : 0;
                String responseBody = response != null ? response.responseJson() : null;

                stepResult = WorkflowStepResult.failure(
                        step.id(),
                        step.name(),
                        interpolatedRequest,
                        responseBody,
                        statusModel,
                        duration,
                        assertionErrors,
                        errorMsg,
                        attempts
                );
            }

            stepResults.add(stepResult);
            stepResultsById.put(step.id(), stepResult);

            // 6. Handle step failure according to FailureStrategy
            if (!stepSuccess) {
                log.warn("Step '{}' failed: {}", step.name(), stepResult.errorMessage());
                FailureStrategy strategy = step.failureStrategy();
                if (strategy == FailureStrategy.CONTINUE_ON_FAILURE) {
                    overallStatus = WorkflowStatus.FAILED;
                    // continue to next step
                } else if (strategy == FailureStrategy.ABORT) {
                    overallStatus = WorkflowStatus.CANCELLED;
                    executionError = "Workflow aborted at step '" + step.name() + "'";
                    break;
                } else { // STOP_ON_FAILURE (default)
                    overallStatus = WorkflowStatus.FAILED;
                    executionError = "Workflow stopped due to failure at step '" + step.name() + "': " + stepResult.errorMessage();
                    break;
                }
            }
        }

        long completedAt = System.currentTimeMillis();
        long durationMs = (System.nanoTime() - startNano) / 1_000_000;

        return new WorkflowExecutionResult(
                executionId,
                workflow.id(),
                workflow.name(),
                overallStatus,
                durationMs,
                stepResults,
                Collections.unmodifiableMap(variables),
                startedAt,
                completedAt,
                executionError
        );
    }
}
