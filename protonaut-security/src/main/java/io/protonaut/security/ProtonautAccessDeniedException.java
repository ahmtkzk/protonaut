package io.protonaut.security;

public class ProtonautAccessDeniedException extends RuntimeException {
    private final ProtonautPermission requiredPermission;

    public ProtonautAccessDeniedException(String message) {
        super(message);
        this.requiredPermission = null;
    }

    public ProtonautAccessDeniedException(ProtonautPermission requiredPermission, String message) {
        super(message);
        this.requiredPermission = requiredPermission;
    }

    public ProtonautPermission getRequiredPermission() {
        return requiredPermission;
    }
}
