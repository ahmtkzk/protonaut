package io.protonaut.security;

public enum ProtonautPermission {
    PROTONAUT_UI_VIEW("Open the Protonaut interface"),
    PROTONAUT_SERVICE_DISCOVER("List services and inspect contracts"),
    PROTONAUT_PROTO_VIEW("View proto definitions and sources"),
    PROTONAUT_REQUEST_EXECUTE("Execute individual gRPC methods"),
    PROTONAUT_WORKFLOW_VIEW("View workflow definitions"),
    PROTONAUT_WORKFLOW_EDIT("Create and edit workflows"),
    PROTONAUT_WORKFLOW_EXECUTE("Execute workflows"),
    PROTONAUT_SETTINGS_MANAGE("Modify privileged settings");

    private final String description;

    ProtonautPermission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
