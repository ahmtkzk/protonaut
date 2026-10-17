# Protonaut

> **Production-Ready gRPC Explorer & Workflow Studio for Spring Boot 4 & Spring Boot 3**

[![Java Version](https://img.shields.io/badge/Java-25%20%2F%2021%20%2F%2017-orange.svg)](https://openjdk.org/)
[![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.0.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.4.2-green.svg)](https://spring.io/projects/spring-boot)
[![gRPC](https://img.shields.io/badge/gRPC-1.71.0-blue.svg)](https://grpc.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

Protonaut is a modern, developer-centric, embedded toolkit for inspecting, executing, and orchestrating gRPC services in Spring Boot applications. Similar to Swagger / OpenAPI UI for REST APIs, Protonaut provides instant browser-based visibility and interactive testing for your gRPC microservices without deploying external gateways, proxies, or standalone backend applications.

---

## 📑 Table of Contents

- [Core Product Vision](#-core-product-vision)
- [Spring Boot 3 vs Spring Boot 4: Requirements & Usage](#-spring-boot-3-vs-spring-boot-4-compatibility--usage-guide)
  - [Feature & Requirement Comparison Matrix](#feature--requirement-comparison-matrix)
  - [Using with Spring Boot 4](#1-using-with-spring-boot-4)
  - [Using with Spring Boot 3](#2-using-with-spring-boot-3)
  - [Critical Note: Compiling Spring Boot 3 with JDK 25](#-critical-requirement-for-spring-boot-3-on-jdk-25)
- [Demo Applications (Spring Boot 3 & 4)](#-demo-applications)
  - [Demo 1: Spring Boot 3 (REST + gRPC + Swagger)](#demo-1-spring-boot-3--rest--grpc--swagger)
  - [Demo 2: Spring Boot 4 (Pure gRPC on Port 6565)](#demo-2-spring-boot-4--pure-grpc-on-port-6565)
  - [Running Both Demos Concurrently](#running-both-demos-side-by-side)
  - [Testing Cross-Service gRPC Interoperability](#testing-cross-service-grpc-interoperability)
- [Architecture & Modules](#️-architecture--modules)
- [Network Ports & Entry Points](#-network-ports--entry-points)
- [Configuration Reference](#️-configuration-reference)
- [Key Features](#-key-features)
  - [Automatic Service Discovery](#automatic-service-discovery)
  - [Interactive gRPC Execution](#interactive-grpc-execution)
  - [Protobuf Schema & Original Source Viewer](#protobuf-schema--original-source-viewer)
  - [Workflow Studio & Assertions](#workflow-studio--assertions)
  - [Multi-Language Client Code Generation](#multi-language-client-code-generation)
- [Production Security & RBAC](#-production-security)
- [Troubleshooting & Diagnostics](#️-troubleshooting--diagnostics)

---

## 🌟 Core Product Vision

Adding the Protonaut starter to any Spring Boot gRPC microservice gives developers and DevOps engineers immediate access to:

1. **Automatic Service Discovery**: Zero-config detection of all registered `BindableService` beans.
2. **Dual-Port Entry Points**: Primary browser entry on Port 6570 (`grpc.port`) and dedicated REST entry on Port 8590 (`rest.port`).
3. **Zero-Latency In-Process Dispatch**: Dispatches calls directly in JVM memory via `InProcessChannel`, eliminating network hops, firewall blocks, and SSRF attack vectors.
4. **Protobuf Viewer**: Displays exact original `.proto` source when available on the classpath, falling back to clean reconstructed proto3 definitions.
5. **Interactive Execution**: Unary and server-streaming RPC invocation with real-time JSON-to-Protobuf translation.
6. **Type-Aware Dummy Data**: Instant payload mocking with sensible defaults for enums, repeated arrays, maps, and oneofs.
7. **Dynamic Metadata Tokens**: Auto-interpolates `{{uuid}}`, `{{timestamp}}`, `{{unixTimestamp}}`, `{{randomString}}`, and `{{sequence}}`.
8. **Client Code Generator**: Generates copy-pasteable client stubs for **Java**, **Go**, **TypeScript**, **Python**, and **C#**.
9. **Visual Workflow Studio**: Multi-step chaining with dynamic variable propagation (`{{steps.stepId.response.path}}`) and AST-free safe expression assertions.
10. **Enterprise Security**: Granular RBAC, execution kill-switches, sensitive token masking, and graceful port conflict degradation.

---

## ☕ Spring Boot 3 vs Spring Boot 4: Compatibility & Usage Guide

Protonaut is designed with a **framework-independent core** (`protonaut-core`, `protonaut-grpc`, `protonaut-security`, `protonaut-workflow`, `protonaut-codegen`, `protonaut-ui`, `protonaut-web`). Framework-specific auto-configuration adapters are cleanly isolated in dedicated starters.

### Feature & Requirement Comparison Matrix

| Aspect | Spring Boot 4 Starter | Spring Boot 3 Starter |
| :--- | :--- | :--- |
| **Starter Artifact** | `protonaut-spring-boot-4-starter` | `protonaut-spring-boot-3-starter` |
| **Spring Boot Version** | `4.0.x` line (tested with `4.0.8`) | `3.2.x` - `3.4.x` (tested with `3.4.2`) |
| **Spring Framework** | `7.x` | `6.x` |
| **Java Baseline** | Java 25 mandatory (`--release 25`) | Java 17+ (Java 17, 21, or 25 bytecode target `--release 21`) |
| **Jakarta EE Version** | Jakarta EE 11 | Jakarta EE 10 |
| **Web Environment** | Works in Pure gRPC (no Web) or WebMVC / WebFlux | Works in Pure gRPC (no Web) or WebMVC / WebFlux |
| **Swagger / OpenAPI** | Compatible with OpenAPI 3.1 specifications | Native compatibility with `springdoc-openapi` 2.x |
| **Auto-Configuration** | `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` | `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` |

---

### 1. Using with Spring Boot 4

#### Prerequisites
- JDK 25 installed.
- Maven `pom.xml` configured with `<maven.compiler.release>25</maven.compiler.release>`.
- Any standard gRPC runtime (e.g. `grpc-netty-shaded`).

#### Dependency
```xml
<dependency>
    <groupId>io.protonaut</groupId>
    <artifactId>protonaut-spring-boot-4-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

#### Application Setup
Implement your gRPC service as a standard Spring bean:
```java
@Service
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {
    @Override
    public void processPayment(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        PaymentResponse response = PaymentResponse.newBuilder()
                .setTransactionId("TX-" + UUID.randomUUID())
                .setStatus("SUCCESS")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```
Protonaut automatically starts its embedded Web UI on `http://localhost:6570/protonaut/`.

---

### 2. Using with Spring Boot 3

#### Prerequisites
- JDK 17, JDK 21, or JDK 25.
- If compiling on JDK 25, compile target must be set to Java 21 or 17 (see below).

#### Dependency
```xml
<dependency>
    <groupId>io.protonaut</groupId>
    <artifactId>protonaut-spring-boot-3-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

#### ⚠️ Critical Requirement for Spring Boot 3 on JDK 25
Spring Boot 3 uses Spring Framework 6.2.x, whose internal ASM `ClassReader` can read class files up to Java 23/24 (class file major version ≤ 68). 

If your development machine uses JDK 25, ensure your Spring Boot 3 `pom.xml` specifies **`<maven.compiler.release>21</maven.compiler.release>`** (or 17):
```xml
<properties>
    <maven.compiler.release>21</maven.compiler.release>
    <java.version>21</java.version>
</properties>
```
*Alternatively, you can pass `-Dspring.classformat.ignore=true` as a JVM argument if scanning higher bytecode versions.*

---

## 🚀 Demo Applications

The repository includes two complete, runnable demo microservices demonstrating cross-service gRPC communication, Swagger integration, and pure gRPC execution:

```text
protonaut-examples/
├── spring-boot-3-rest-grpc/     # Demo 1: Spring Boot 3 (REST + Swagger + gRPC on 9090)
└── spring-boot-4-grpc-only/     # Demo 2: Spring Boot 4 (Pure gRPC on 6565, Zero HTTP)
```

### Demo 1: Spring Boot 3 — REST + gRPC + Swagger
- **Framework**: Spring Boot 3.4.2 (Java 21 bytecode target)
- **REST Port**: `8080`
- **Swagger / OpenAPI UI**: [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)
- **gRPC Server Port**: `9090` (Warehouse & Logistics domain)
- **Protonaut Explorer**: [`http://localhost:8590/protonaut/`](http://localhost:8590/protonaut/)
- **gRPC Services**:
  - `InventoryService` (`CheckStock`, `ReserveStock`)
  - `ShipmentService` (`TrackShipment`)
- **REST Controller**: `WarehouseRestController`
  - `GET /api/warehouse/stock/{sku}` — Invokes in-process local `InventoryService`.
  - `POST /api/warehouse/checkout` — Reserves local stock AND makes an **external gRPC client call to Demo 2 on Port 6565** (`PaymentServiceGrpc.processPayment`)!

### Demo 2: Spring Boot 4 — Pure gRPC on Port 6565
- **Framework**: Spring Boot 4.0.8 (Java 25)
- **Pure gRPC Server Port**: **`6565`** (`spring.main.web-application-type: none`, zero Tomcat/HTTP servlet overhead)
- **Protonaut Explorer**: [`http://localhost:6570/protonaut/`](http://localhost:6570/protonaut/)
- **gRPC Services**:
  - `PaymentService` (`ProcessPayment`, `RefundPayment`)
  - `WalletService` (`GetBalance`, `TransferFunds`)
- **Cross-Service gRPC Call**:
  - When `PaymentService.processPayment` is invoked on Port 6565, it calls **Demo 1 on Port 9090** (`InventoryServiceGrpc.checkStock`) over gRPC to verify stock availability before confirming payment!

---

### Running Both Demos Side-by-Side

Open two terminal windows:

#### Terminal 1: Start Demo 2 (Spring Boot 4 Pure gRPC on Port 6565)
```powershell
$env:JAVA_HOME = "C:\Users\ahmet\.jdks\temurin-25"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
.\mvnw.cmd spring-boot:run -pl protonaut-examples/spring-boot-4-grpc-only
```
- gRPC Port: `localhost:6565`
- Protonaut Explorer: [`http://localhost:6570/protonaut/`](http://localhost:6570/protonaut/)

#### Terminal 2: Start Demo 1 (Spring Boot 3 REST + gRPC + Swagger)
```powershell
$env:JAVA_HOME = "C:\Users\ahmet\.jdks\temurin-25"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
.\mvnw.cmd spring-boot:run -pl protonaut-examples/spring-boot-3-rest-grpc
```
- REST & Swagger: [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)
- gRPC Port: `localhost:9090`
- Protonaut Explorer: [`http://localhost:8590/protonaut/`](http://localhost:8590/protonaut/)

---

### Testing Cross-Service gRPC Interoperability

#### 1. Interactive Testing via Swagger UI
1. Navigate to [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html).
2. Expand `POST /api/warehouse/checkout`.
3. Click **Try it out** and execute:
```json
{
  "customerId": "CUST-100",
  "sku": "SKU-LAPTOP-01",
  "quantity": 2,
  "amount": 199.98,
  "currency": "USD"
}
```
4. You will receive an immediate HTTP 200 response demonstrating multi-hop cross-service gRPC execution:
```json
{
  "orderId": "ORD-5D9A9F75",
  "reservation": {
    "reservationId": "RES-7A30ABA7",
    "sku": "SKU-LAPTOP-01",
    "reservedQuantity": 2,
    "status": "CONFIRMED"
  },
  "remotePaymentGrpc": {
    "transactionId": "TX-E4A4AC27-1",
    "status": "SUCCESS",
    "message": "Payment of 199.98 USD captured successfully. Verified with downstream inventory.",
    "targetGrpcPort": 6565
  },
  "architectureNote": "Cross-service interoperability: REST endpoint invoked local Inventory gRPC and remote Payment gRPC (port 6565)."
}
```

#### 2. Interactive Testing via Protonaut UI
- Visit [`http://localhost:6570/protonaut/`](http://localhost:6570/protonaut/) to test `PaymentService` and `WalletService` on Spring Boot 4.
- Visit [`http://localhost:8590/protonaut/`](http://localhost:8590/protonaut/) to test `InventoryService` and `ShipmentService` on Spring Boot 3.

---

## 🏗️ Architecture & Modules

```text
protonaut/
├── protonaut-bom/                          # Bill of Materials
├── protonaut-core/                         # Framework-independent domain models & SPIs
├── protonaut-grpc/                         # Protobuf converters, descriptors, executor
├── protonaut-security/                     # RBAC permissions, principal, token masking
├── protonaut-codegen/                      # Multi-language client code generators
├── protonaut-workflow/                     # Workflow engine & safe expression evaluator
├── protonaut-ui/                           # Embedded React 18, TypeScript, Monaco SPA
├── protonaut-web/                          # Embedded Netty server & REST dispatcher
│
├── protonaut-spring-boot-4-autoconfigure/  # Spring Boot 4 (Java 25, Spring 7) auto-config
├── protonaut-spring-boot-4-starter/        # Single starter for Spring Boot 4 applications
│
├── protonaut-spring-boot-3-autoconfigure/  # Spring Boot 3 (Java 21/17, Spring 6) auto-config
├── protonaut-spring-boot-3-starter/        # Single starter for Spring Boot 3 applications
│
├── protonaut-examples/                     # Sample applications
│   ├── spring-boot-3-rest-grpc/            # Demo 1: REST + Swagger + gRPC on 9090
│   ├── spring-boot-4-grpc-only/            # Demo 2: Pure gRPC on Port 6565
│   └── spring-boot-4-grpc-example/         # Multi-service reference example
└── protonaut-integration-tests/            # Comprehensive end-to-end IT suite
```

---

## 🌐 Network Ports & Entry Points

| Port | Config Key | Description | Status |
| :--- | :--- | :--- | :--- |
| **6570** | `protonaut.grpc.port` | **Primary Browser Entry Point**. Serves the embedded React SPA and REST APIs. | Always active when Protonaut is enabled. |
| **8590** | `protonaut.rest.port` | **Dedicated REST Entry Point**. Serves all `/protonaut/api/*` endpoints. | Controlled by `protonaut.rest.enabled`. |

### Graceful Port Conflict Handling
If port 6570 or 8590 is already occupied by another service on the host, Protonaut detects the collision, logs a clean warning (`[Protonaut] WARN - Port is already in use`), and allows the host application to finish starting its core business logic normally (unless `protonaut.execution.fail-fast-on-port-conflict: true` is set).

---

## ⚙️ Configuration Reference

All settings can be declared in `application.yml` or `application.properties`:

```yaml
protonaut:
  enabled: true                        # Master toggle for Protonaut
  
  grpc:
    port: 6570                         # Browser UI & API port
    host: "0.0.0.0"                    # Bind address
    
  rest:
    enabled: true                      # Dedicated REST listener toggle
    port: 8590                         # Dedicated REST port
    host: "0.0.0.0"
    
  discovery:
    enabled: true
    reflection-enabled: true           # Inspect runtime reflection if available
    include-services: []               # Whitelist filter patterns (e.g. "io.company.*")
    exclude-services: []               # Blacklist filter patterns
    
  security:
    mode: "ANONYMOUS"                  # "ANONYMOUS", "DEV", or "RBAC"
    require-authentication: false      # Enforce Bearer token authentication
    allow-anonymous-local-development: true
    mask-sensitive-values: true        # Mask Authorization, ApiKey, Cookies in UI
    
  execution:
    enabled: true                      # Global interactive execution kill-switch
    timeout-seconds: 30                # Default gRPC deadline
    max-request-bytes: 1048576         # 1 MB maximum payload limit
    max-concurrent-requests: 50        # Concurrency throttle
    fail-fast-on-port-conflict: false  # If true, crashes application on port collision
    
  homepage:
    project-name: "Order Microservice"
    team-name: "Core Platform Team"
    environment: "Local"
    version: "1.0.0"
    description: "Enterprise gRPC Services"
```

---

## 🔒 Production Security

In production environments, activate RBAC mode:

```yaml
protonaut:
  security:
    mode: "RBAC"
    require-authentication: true
    allow-anonymous-local-development: false
```

Pass authentication tokens via HTTP header:
`Authorization: Bearer <token>`

- **Viewer Tokens**: Allow inspecting schemas, documentation, and services (`PROTONAUT_VIEWER`). Execution is blocked with `403 Forbidden`.
- **Admin Tokens**: Grant execution and workflow editing privileges (`PROTONAUT_ADMIN`).
- **Emergency Kill Switch**: Set `protonaut.execution.enabled: false` to immediately prevent all RPC execution cluster-wide without restarting application servers.

---

## 🛠️ Troubleshooting & Diagnostics

| Symptom | Diagnostic Cause | Resolution |
| :--- | :--- | :--- |
| `Unsupported class file major version 69` in Spring Boot 3 | JDK 25 compiled bytecode (version 69) loaded by Spring Framework 6.2 | Add `<maven.compiler.release>21</maven.compiler.release>` to your Spring Boot 3 `pom.xml`. |
| `No gRPC service beans detected` | No beans implement `BindableService` | Ensure your service extends `*Grpc.*ImplBase` and has `@Service` or `@Component`. |
| Port 6570 returns 403 Forbidden | Security is set to RBAC mode | Supply a Bearer token or configure `mode: ANONYMOUS` for local development. |
| Reconstructed proto shown instead of original | `.proto` files are not on classpath | Include your `.proto` files in `src/main/resources/proto/`. |
| Port already in use warning on startup | Another application has claimed port 6570 | Change `protonaut.grpc.port` or let Protonaut skip binding gracefully. |

---

## 📄 License

Licensed under the [Apache License, Version 2.0](LICENSE).
