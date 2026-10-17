package io.protonaut.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record ProtonautPrincipal(
        String username,
        Set<String> roles,
        Set<ProtonautPermission> permissions,
        boolean authenticated
) {
    public ProtonautPrincipal {
        roles = roles != null ? Set.copyOf(roles) : Collections.emptySet();
        permissions = permissions != null ? Set.copyOf(permissions) : Collections.emptySet();
    }

    public boolean hasPermission(ProtonautPermission permission) {
        if (permission == null) return false;
        return permissions.contains(permission);
    }

    public static ProtonautPrincipal anonymous() {
        return new ProtonautPrincipal("anonymous", Set.of(), Set.of(), false);
    }

    public static ProtonautPrincipal admin(String username) {
        return new ProtonautPrincipal(
                username != null ? username : "admin",
                Set.of("ROLE_PROTONAUT_ADMIN"),
                Set.of(ProtonautPermission.values()),
                true
        );
    }

    public static ProtonautPrincipal ofRole(String username, ProtonautRole role) {
        return new ProtonautPrincipal(
                username != null ? username : "user",
                Set.of("ROLE_" + role.name()),
                role.getDefaultPermissions(),
                true
        );
    }
}
