package io.protonaut.core.spi;

import io.protonaut.core.models.WorkflowDefinition;

import java.util.List;
import java.util.Optional;

public interface WorkflowRepository {
    List<WorkflowDefinition> findAll();

    Optional<WorkflowDefinition> findById(String id);

    WorkflowDefinition save(WorkflowDefinition workflow);

    boolean deleteById(String id);
}
