# Protonaut Deployment & Kubernetes Ingress

## 1. Containerization (Dockerfile)

Below is an enterprise multi-stage Dockerfile leveraging Eclipse Temurin Java 25:

```dockerfile
# Build Stage
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /workspace

# Install Node.js for UI compilation
RUN apk add --no-cache nodejs npm

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY protonaut-bom protonaut-bom
COPY protonaut-core protonaut-core
COPY protonaut-grpc protonaut-grpc
COPY protonaut-security protonaut-security
COPY protonaut-codegen protonaut-codegen
COPY protonaut-workflow protonaut-workflow
COPY protonaut-ui protonaut-ui
COPY protonaut-web protonaut-web
COPY protonaut-spring-boot-4-autoconfigure protonaut-spring-boot-4-autoconfigure
COPY protonaut-spring-boot-4-starter protonaut-spring-boot-4-starter
COPY protonaut-examples protonaut-examples

# Build Web UI
RUN cd protonaut-ui && npm ci && npm run build

# Package JAR
RUN ./mvnw clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Non-root service user
RUN addgroup -S protonaut && adduser -S protonaut -G protonaut
USER protonaut:protonaut

COPY --from=builder /workspace/protonaut-examples/spring-boot-4-grpc-example/target/*.jar app.jar

# Expose gRPC business port, Protonaut UI port, and dedicated REST port
EXPOSE 9090 6570 8590

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 2. Kubernetes Deployment & Ingress

### Service Definition

```yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service
  labels:
    app: order-service
spec:
  type: ClusterIP
  ports:
    - name: grpc-business
      port: 9090
      targetPort: 9090
      appProtocol: grpc
    - name: protonaut-ui
      port: 6570
      targetPort: 6570
      appProtocol: http
    - name: protonaut-rest
      port: 8590
      targetPort: 8590
      appProtocol: http
  selector:
    app: order-service
```

### Ingress Manifest (Subpath Routing)

Protonaut uses purely relative asset URLs, enabling effortless reverse proxying under any URL prefix:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: order-service-protonaut-ingress
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/proxy-read-timeout: "3600"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "3600"
spec:
  rules:
    - host: api-explorer.internal.company.com
      http:
        paths:
          - path: /order-service/protonaut
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 6570
```
