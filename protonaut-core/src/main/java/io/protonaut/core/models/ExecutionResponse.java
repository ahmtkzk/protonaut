package io.protonaut.core.models;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record ExecutionResponse(
        String requestId,
        String serviceId,
        String methodId,
        String responseJson,
        GrpcStatusModel grpcStatus,
        long durationMs,
        long responseSizeBytes,
        Map<String, String> responseMetadata,
        Map<String, String> trailingMetadata,
        List<String> streamingResponses,
        boolean success,
        String errorMessage
) {
    public ExecutionResponse {
        responseMetadata = responseMetadata != null ? Map.copyOf(responseMetadata) : Collections.emptyMap();
        trailingMetadata = trailingMetadata != null ? Map.copyOf(trailingMetadata) : Collections.emptyMap();
        streamingResponses = streamingResponses != null ? List.copyOf(streamingResponses) : Collections.emptyList();
    }

    public static ExecutionResponse success(
            String requestId,
            String serviceId,
            String methodId,
            String responseJson,
            long durationMs,
            long responseSizeBytes,
            Map<String, String> responseMetadata,
            Map<String, String> trailingMetadata
    ) {
        return new ExecutionResponse(
                requestId,
                serviceId,
                methodId,
                responseJson,
                GrpcStatusModel.OK,
                durationMs,
                responseSizeBytes,
                responseMetadata,
                trailingMetadata,
                List.of(),
                true,
                null
        );
    }

    public static ExecutionResponse failure(
            String requestId,
            String serviceId,
            String methodId,
            GrpcStatusModel status,
            long durationMs,
            String errorMessage,
            Map<String, String> responseMetadata,
            Map<String, String> trailingMetadata
    ) {
        return new ExecutionResponse(
                requestId,
                serviceId,
                methodId,
                null,
                status,
                durationMs,
                0,
                responseMetadata,
                trailingMetadata,
                List.of(),
                false,
                errorMessage
        );
    }
}
