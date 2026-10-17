package io.protonaut.security;

import java.util.Set;

public enum ProtonautRole {
    PROTONAUT_VIEWER(Set.of(
            ProtonautPermission.PROTONAUT_UI_VIEW,
            ProtonautPermission.PROTONAUT_SERVICE_DISCOVER,
            ProtonautPermission.PROTONAUT_PROTO_VIEW,
            ProtonautPermission.PROTONAUT_WORKFLOW_VIEW
    )),
    PROTONAUT_TESTER(Set.of(
            ProtonautPermission.PROTONAUT_UI_VIEW,
            ProtonautPermission.PROTONAUT_SERVICE_DISCOVER,
            ProtonautPermission.PROTONAUT_PROTO_VIEW,
            ProtonautPermission.PROTONAUT_REQUEST_EXECUTE,
            ProtonautPermission.PROTONAUT_WORKFLOW_VIEW
    )),
    PROTONAUT_WORKFLOW_EDITOR(Set.of(
            ProtonautPermission.PROTONAUT_UI_VIEW,
            ProtonautPermission.PROTONAUT_SERVICE_DISCOVER,
            ProtonautPermission.PROTONAUT_PROTO_VIEW,
            ProtonautPermission.PROTONAUT_WORKFLOW_VIEW,
            ProtonautPermission.PROTONAUT_WORKFLOW_EDIT
    )),
    PROTONAUT_ADMIN(Set.of(
            ProtonautPermission.values()
    ));

    private final Set<ProtonautPermission> defaultPermissions;

    ProtonautRole(Set<ProtonautPermission> defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }

    public Set<ProtonautPermission> getDefaultPermissions() {
        return defaultPermissions;
    }
}
