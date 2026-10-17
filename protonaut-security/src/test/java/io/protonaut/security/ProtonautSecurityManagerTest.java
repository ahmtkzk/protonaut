package io.protonaut.security;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProtonautSecurityManagerTest {

    @Test
    void testViewerCannotExecuteMethods() {
        ProtonautSecurityManager manager = new ProtonautSecurityManager(SecurityConfigModel.defaultProduction());
        ProtonautPrincipal viewer = ProtonautPrincipal.ofRole("alice", ProtonautRole.PROTONAUT_VIEWER);

        // Viewer can view UI
        manager.checkPermission(viewer, ProtonautPermission.PROTONAUT_UI_VIEW);

        // Viewer CANNOT execute
        assertThatThrownBy(() -> manager.checkRequestExecution(viewer, 100))
                .isInstanceOf(ProtonautAccessDeniedException.class)
                .hasMessageContaining("does not have permission: PROTONAUT_REQUEST_EXECUTE");
    }

    @Test
    void testTesterCanExecuteMethods() {
        ProtonautSecurityManager manager = new ProtonautSecurityManager(SecurityConfigModel.defaultProduction());
        ProtonautPrincipal tester = ProtonautPrincipal.ofRole("bob", ProtonautRole.PROTONAUT_TESTER);

        // Tester CAN execute
        manager.checkRequestExecution(tester, 100);

        // But tester CANNOT edit workflows
        assertThatThrownBy(() -> manager.checkPermission(tester, ProtonautPermission.PROTONAUT_WORKFLOW_EDIT))
                .isInstanceOf(ProtonautAccessDeniedException.class);
    }

    @Test
    void testExecutionDisabledEnforcedOnBackend() {
        SecurityConfigModel config = new SecurityConfigModel(
                "secure", true, false, false, true,
                false, // execution disabled!
                true,
                1000, 1000, 10
        );
        ProtonautSecurityManager manager = new ProtonautSecurityManager(config);
        ProtonautPrincipal admin = ProtonautPrincipal.admin("admin");

        assertThatThrownBy(() -> manager.checkRequestExecution(admin, 50))
                .isInstanceOf(ProtonautAccessDeniedException.class)
                .hasMessageContaining("Request execution is disabled by server configuration");
    }

    @Test
    void testWorkflowExecutionDisabledEnforced() {
        SecurityConfigModel config = new SecurityConfigModel(
                "secure", true, false, false, true,
                true,
                false, // workflow execution disabled!
                1000, 1000, 10
        );
        ProtonautSecurityManager manager = new ProtonautSecurityManager(config);
        ProtonautPrincipal admin = ProtonautPrincipal.admin("admin");

        assertThatThrownBy(() -> manager.checkWorkflowExecution(admin))
                .isInstanceOf(ProtonautAccessDeniedException.class)
                .hasMessageContaining("Workflow execution is disabled by server configuration");
    }

    @Test
    void testMaskSensitiveHeaders() {
        ProtonautSecurityManager manager = new ProtonautSecurityManager(SecurityConfigModel.defaultProduction());
        Map<String, String> headers = Map.of(
                "Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.secret123",
                "x-api-key", "my-secret-key-9999",
                "x-correlation-id", "corr-123"
        );

        Map<String, String> masked = manager.maskSensitiveHeaders(headers);

        assertThat(masked.get("Authorization")).doesNotContain("secret123");
        assertThat(masked.get("x-api-key")).doesNotContain("9999");
        assertThat(masked.get("x-correlation-id")).isEqualTo("corr-123");
    }
}
