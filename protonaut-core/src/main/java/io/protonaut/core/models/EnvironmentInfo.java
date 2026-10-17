package io.protonaut.core.models;

import java.util.Map;

public record EnvironmentInfo(
        String projectName,
        String teamName,
        String environment,
        String version,
        String description,
        int grpcPort,
        int restPort,
        boolean restEnabled,
        String host,
        String status,
        Map<String, String> usefulLinks
) {}
