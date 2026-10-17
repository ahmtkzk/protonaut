package io.protonaut.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Semaphore;

public class ProtonautSecurityManager {
    private static final Logger log = LoggerFactory.getLogger(ProtonautSecurityManager.class);

    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization",
            "proxy-authorization",
            "cookie",
            "set-cookie",
            "x-api-key",
            "apikey",
            "api-key",
            "secret",
            "x-auth-token"
    );

    private final SecurityConfigModel config;
    private final Semaphore concurrencyLimiter;

    public ProtonautSecurityManager(SecurityConfigModel config) {
        this.config = config != null ? config : SecurityConfigModel.defaultProduction();
        this.concurrencyLimiter = new Semaphore(this.config.maxConcurrentRequests());
    }

    public void checkPermission(ProtonautPrincipal principal, ProtonautPermission permission) {
        if (permission == null) return;

        // If in development/anonymous mode with anonymous access allowed:
        boolean isDevMode = "development".equalsIgnoreCase(config.mode()) ||
                "dev".equalsIgnoreCase(config.mode()) ||
                "anonymous".equalsIgnoreCase(config.mode()) ||
                "anonymous_dev".equalsIgnoreCase(config.mode()) ||
                !config.requireAuthentication();
        if (isDevMode && config.allowAnonymousLocalDevelopment()) {
            return;
        }

        if (principal == null || !principal.authenticated()) {
            if (config.requireAuthentication()) {
                throw new ProtonautAccessDeniedException(permission, "Authentication required to access " + permission.name());
            }
        }

        if (principal != null && principal.hasPermission(permission)) {
            return;
        }

        throw new ProtonautAccessDeniedException(permission, "Principal '" + (principal != null ? principal.username() : "anonymous") +
                "' does not have permission: " + permission.name());
    }

    public void checkRequestExecution(ProtonautPrincipal principal, long requestSizeBytes) {
        if (!config.executionEnabled()) {
            throw new ProtonautAccessDeniedException(
                    ProtonautPermission.PROTONAUT_REQUEST_EXECUTE,
                    "Request execution is disabled by server configuration (protonaut.execution.enabled = false)"
            );
        }

        if (requestSizeBytes > config.maxRequestBytes()) {
            throw new IllegalArgumentException("Request size (" + requestSizeBytes + " bytes) exceeds configured limit of " + config.maxRequestBytes() + " bytes");
        }

        checkPermission(principal, ProtonautPermission.PROTONAUT_REQUEST_EXECUTE);
    }

    public void checkWorkflowExecution(ProtonautPrincipal principal) {
        if (!config.workflowExecutionEnabled()) {
            throw new ProtonautAccessDeniedException(
                    ProtonautPermission.PROTONAUT_WORKFLOW_EXECUTE,
                    "Workflow execution is disabled by server configuration (protonaut.workflow.execution-enabled = false)"
            );
        }

        checkPermission(principal, ProtonautPermission.PROTONAUT_WORKFLOW_EXECUTE);
    }

    public boolean tryAcquireExecutionSlot() {
        return concurrencyLimiter.tryAcquire();
    }

    public void releaseExecutionSlot() {
        concurrencyLimiter.release();
    }

    public Map<String, String> maskSensitiveHeaders(Map<String, String> headers) {
        if (headers == null || headers.isEmpty() || !config.maskSensitiveValues()) {
            return headers != null ? headers : Map.of();
        }

        Map<String, String> masked = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && SENSITIVE_HEADERS.contains(key.trim().toLowerCase())) {
                masked.put(key, maskSecret(value));
            } else {
                masked.put(key, value);
            }
        }
        return masked;
    }

    public static String maskSecret(String secret) {
        if (secret == null || secret.isBlank()) return "***";
        return "********";
    }

    public SecurityConfigModel getConfig() {
        return config;
    }
}
