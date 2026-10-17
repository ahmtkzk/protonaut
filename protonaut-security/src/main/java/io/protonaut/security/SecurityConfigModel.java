package io.protonaut.security;

public record SecurityConfigModel(
        String mode,
        boolean requireAuthentication,
        boolean allowAnonymousLocalDevelopment,
        boolean allowBrowserTokenStorage,
        boolean maskSensitiveValues,
        boolean executionEnabled,
        boolean workflowExecutionEnabled,
        long maxRequestBytes,
        long maxResponseBytes,
        int maxConcurrentRequests
) {
    public static SecurityConfigModel defaultProduction() {
        return new SecurityConfigModel(
                "secure",
                true,
                false,
                false,
                true,
                true,
                true,
                1_048_576L,     // 1MB
                4_194_304L,     // 4MB
                20
        );
    }

    public static SecurityConfigModel localDevelopment() {
        return new SecurityConfigModel(
                "development",
                false,
                true,
                true,
                true,
                true,
                true,
                5_242_880L,     // 5MB
                10_485_760L,    // 10MB
                50
        );
    }
}
