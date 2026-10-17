package io.protonaut.core.spi;

import io.protonaut.core.models.WorkflowDefinition;
import io.protonaut.core.models.WorkflowExecutionResult;

public interface WorkflowEngine {
    /**
     * Executes the given workflow definition.
     *
     * @param workflow the workflow definition with steps and assertions
     * @return the execution result with step-by-step telemetry
     */
    WorkflowExecutionResult execute(WorkflowDefinition workflow);
}
