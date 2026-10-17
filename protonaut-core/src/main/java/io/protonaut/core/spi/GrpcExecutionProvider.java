package io.protonaut.core.spi;

import io.protonaut.core.models.ExecutionRequest;
import io.protonaut.core.models.ExecutionResponse;

public interface GrpcExecutionProvider {
    /**
     * Executes a gRPC method based on the given request.
     *
     * @param request the execution request containing service, method, payload and metadata
     * @return the execution response with gRPC status, body, latency and metadata
     */
    ExecutionResponse execute(ExecutionRequest request);
}
