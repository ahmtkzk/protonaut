# Protonaut Security Architecture

## 1. Threat Model & Design Goals

Embedded developer tools introduced into production environments present significant security risks if not architected defensively:
1. **Accidental Mutation**: Unauthorized developers triggering state-mutating RPC calls in staging or production.
2. **Credential Leaks**: Authorization headers, cookies, and API keys stored in plain text or rendered on screens during demos.
3. **Denial of Service**: Malformed JSON payloads or runaway concurrency exhausting JVM resources.
4. **Server-Side Request Forgery (SSRF)**: Specifying arbitrary target hostnames to pivot into internal VPC infrastructure.

---

## 2. Granular RBAC Permissions

Protonaut separates system privileges into fine-grained permissions:

| Permission | Description |
| :--- | :--- |
| `PROTONAUT_UI_VIEW` | View the dashboard and system environment |
| `PROTONAUT_SERVICE_DISCOVER` | List discovered gRPC services and methods |
| `PROTONAUT_PROTO_VIEW` | Read original and reconstructed `.proto` schemas |
| `PROTONAUT_REQUEST_EXECUTE` | Execute interactive gRPC requests |
| `PROTONAUT_WORKFLOW_VIEW` | View saved workflow execution graphs |
| `PROTONAUT_WORKFLOW_EDIT` | Create, modify, and delete workflow chains |
| `PROTONAUT_WORKFLOW_EXECUTE` | Trigger sequential workflow automation |
| `PROTONAUT_SETTINGS_MANAGE` | Modify explorer runtime configuration |

### Predefined Roles

- **`PROTONAUT_VIEWER`**: Read-only exploration (`UI_VIEW`, `SERVICE_DISCOVER`, `PROTO_VIEW`, `WORKFLOW_VIEW`). Cannot execute RPCs or mutate workflows.
- **`PROTONAUT_TESTER`**: Viewer privileges plus `REQUEST_EXECUTE` and `WORKFLOW_EXECUTE`.
- **`PROTONAUT_OPERATOR`**: Full operational permissions excluding system administrative policy management.
- **`PROTONAUT_ADMIN`**: Unrestricted privileges across all capabilities.

---

## 3. Production Protection Switches

### Global Execution Kill-Switch

In production clusters, interactive execution can be completely disabled by setting:

```yaml
protonaut:
  execution:
    enabled: false
  workflow:
    execution-enabled: false
```

When disabled, all execution attempts are rejected at the security manager boundary with `403 Forbidden`, regardless of credentials.

### Sensitive Token Masking

Headers such as `authorization`, `x-api-key`, `cookie`, `secret`, and `proxy-authorization` are automatically masked before being persisted in history or returned in API responses:

```json
{
  "x-trace-id": "e2801402-9130-4e3a-9e12-4c60e3d5ef58",
  "authorization": "********"
}
```

### Rate & Concurrency Limiting

Protonaut enforces a strict concurrency ceiling (`protonaut.execution.max-concurrent-requests: 50`) using high-throughput non-blocking semaphores, preventing runaway load from impacting core business workloads.
