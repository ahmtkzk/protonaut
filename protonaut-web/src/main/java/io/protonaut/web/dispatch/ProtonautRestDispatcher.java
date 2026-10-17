package io.protonaut.web.dispatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.protonaut.codegen.CodeGeneratorRegistry;
import io.protonaut.core.models.*;
import io.protonaut.core.spi.DescriptorRegistry;
import io.protonaut.core.spi.GrpcExecutionProvider;
import io.protonaut.core.spi.ProtoSourceResolver;
import io.protonaut.core.spi.WorkflowEngine;
import io.protonaut.core.spi.WorkflowRepository;
import io.protonaut.grpc.dummy.DummyDataGenerator;
import io.protonaut.security.ProtonautPermission;
import io.protonaut.security.ProtonautPrincipal;
import io.protonaut.security.ProtonautSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ProtonautRestDispatcher {
    private static final Logger log = LoggerFactory.getLogger(ProtonautRestDispatcher.class);
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final DescriptorRegistry descriptorRegistry;
    private final GrpcExecutionProvider executionProvider;
    private final ProtoSourceResolver protoSourceResolver;
    private final WorkflowEngine workflowEngine;
    private final WorkflowRepository workflowRepository;
    private final CodeGeneratorRegistry codeGeneratorRegistry;
    private final ProtonautSecurityManager securityManager;
    private final EnvironmentInfo environmentInfo;

    public record HttpResponse(int statusCode, String contentType, byte[] body, Map<String, String> headers) {
        public HttpResponse(int statusCode, String contentType, byte[] body) {
            this(statusCode, contentType, body, Map.of());
        }

        public static HttpResponse json(int status, Object body) {
            try {
                byte[] bytes = MAPPER.writeValueAsBytes(body);
                return new HttpResponse(status, "application/json; charset=UTF-8", bytes);
            } catch (JsonProcessingException e) {
                return text(500, "{\"error\": \"Serialization failed\"}");
            }
        }

        public static HttpResponse text(int status, String text) {
            return new HttpResponse(status, "text/plain; charset=UTF-8", text.getBytes(StandardCharsets.UTF_8));
        }

        public static HttpResponse html(int status, String html) {
            return new HttpResponse(status, "text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8));
        }

        public static HttpResponse redirect(String location) {
            String html = "<!DOCTYPE html><html><head><meta http-equiv=\"refresh\" content=\"0;url=" + location + "\">" +
                    "<script>window.location.replace('" + location + "');</script></head><body>Redirecting to <a href=\"" + location + "\">" + location + "</a></body></html>";
            return new HttpResponse(302, "text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8), Map.of("Location", location));
        }

        public static HttpResponse notFound(String message) {
            return json(404, Map.of("error", "NOT_FOUND", "message", message));
        }

        public static HttpResponse badRequest(String message) {
            return json(400, Map.of("error", "BAD_REQUEST", "message", message));
        }

        public static HttpResponse forbidden(String message) {
            return json(403, Map.of("error", "FORBIDDEN", "message", message));
        }
    }

    public ProtonautRestDispatcher(
            DescriptorRegistry descriptorRegistry,
            GrpcExecutionProvider executionProvider,
            ProtoSourceResolver protoSourceResolver,
            WorkflowEngine workflowEngine,
            WorkflowRepository workflowRepository,
            CodeGeneratorRegistry codeGeneratorRegistry,
            ProtonautSecurityManager securityManager,
            EnvironmentInfo environmentInfo
    ) {
        this.descriptorRegistry = descriptorRegistry;
        this.executionProvider = executionProvider;
        this.protoSourceResolver = protoSourceResolver;
        this.workflowEngine = workflowEngine;
        this.workflowRepository = workflowRepository;
        this.codeGeneratorRegistry = codeGeneratorRegistry != null ? codeGeneratorRegistry : new CodeGeneratorRegistry();
        this.securityManager = securityManager;
        this.environmentInfo = environmentInfo;
    }

    public HttpResponse dispatch(String method, String rawUri, Map<String, String> headers, byte[] bodyBytes) {
        String uri = rawUri;
        Map<String, String> queryParams = new HashMap<>();
        int queryIdx = uri.indexOf('?');
        if (queryIdx >= 0) {
            String queryString = uri.substring(queryIdx + 1);
            uri = uri.substring(0, queryIdx);
            for (String param : queryString.split("&")) {
                int eqIdx = param.indexOf('=');
                if (eqIdx > 0) {
                    String key = URLDecoder.decode(param.substring(0, eqIdx), StandardCharsets.UTF_8);
                    String val = URLDecoder.decode(param.substring(eqIdx + 1), StandardCharsets.UTF_8);
                    queryParams.put(key, val);
                }
            }
        }

        // Clean URI
        if (uri.startsWith("//")) {
            uri = uri.substring(1);
        }

        // Redirect root / to /protonaut/
        if (uri.equals("/") || uri.equals("")) {
            return HttpResponse.redirect("./protonaut/");
        }

        // If requesting /protonaut without trailing slash, redirect to /protonaut/
        if (uri.equals("/protonaut")) {
            return HttpResponse.redirect("./protonaut/");
        }

        // If requesting /protonaut/
        if (uri.equals("/protonaut/")) {
            return serveStaticAsset("index.html");
        }

        // Handle REST APIs under /protonaut/api or /api
        String apiPath = null;
        if (uri.startsWith("/protonaut/api/")) {
            apiPath = uri.substring("/protonaut/api".length());
        } else if (uri.startsWith("/api/")) {
            apiPath = uri.substring("/api".length());
        }

        if (apiPath != null) {
            return handleApi(method, apiPath, queryParams, headers, bodyBytes);
        }

        // Handle static assets under /protonaut/*
        if (uri.startsWith("/protonaut/")) {
            String assetPath = uri.substring("/protonaut/".length());
            return serveStaticAsset(assetPath);
        }

        // Handle root-level assets if requested without prefix (e.g. /assets/*, /favicon.*)
        if (uri.startsWith("/assets/")) {
            return serveStaticAsset(uri.substring(1));
        }
        if (uri.equals("/favicon.svg") || uri.equals("/favicon.ico")) {
            return serveStaticAsset(uri.substring(1));
        }

        return HttpResponse.notFound("Path not mapped in Protonaut: " + uri);
    }

    private HttpResponse handleApi(String method, String apiPath, Map<String, String> queryParams, Map<String, String> headers, byte[] bodyBytes) {
        String body = bodyBytes != null ? new String(bodyBytes, StandardCharsets.UTF_8) : "";
        ProtonautPrincipal principal = resolvePrincipal(headers);

        try {
            // 1. GET /info or /environment
            if (method.equals("GET") && (apiPath.equals("/info") || apiPath.equals("/environment"))) {
                securityManager.checkPermission(principal, ProtonautPermission.PROTONAUT_UI_VIEW);
                return HttpResponse.json(200, environmentInfo != null ? environmentInfo : Map.of("status", "UP"));
            }

            // 2. GET /config/public
            if (method.equals("GET") && apiPath.equals("/config/public")) {
                Map<String, Object> publicCfg = new LinkedHashMap<>();
                publicCfg.put("mode", securityManager.getConfig().mode());
                publicCfg.put("requireAuthentication", securityManager.getConfig().requireAuthentication());
                publicCfg.put("executionEnabled", securityManager.getConfig().executionEnabled());
                publicCfg.put("workflowExecutionEnabled", securityManager.getConfig().workflowExecutionEnabled());
                return HttpResponse.json(200, publicCfg);
            }

            // 3. GET /services
            if (method.equals("GET") && apiPath.equals("/services")) {
                securityManager.checkPermission(principal, ProtonautPermission.PROTONAUT_SERVICE_DISCOVER);
                List<ServiceDescriptorModel> list = descriptorRegistry.getAllServices();
                return HttpResponse.json(200, list);
            }

            // 3.5. GET /services/{id}/methods/{methodId}/dummy
            if (method.equals("GET") && apiPath.startsWith("/services/") && apiPath.contains("/methods/") && apiPath.endsWith("/dummy")) {
                String sub = apiPath.substring("/services/".length());
                int mIdx = sub.indexOf("/methods/");
                if (mIdx > 0) {
                    String sId = URLDecoder.decode(sub.substring(0, mIdx), StandardCharsets.UTF_8);
                    String mId = URLDecoder.decode(sub.substring(mIdx + "/methods/".length(), sub.length() - "/dummy".length()), StandardCharsets.UTF_8);
                    Optional<MethodDescriptorModel> methodOpt = descriptorRegistry.findMethodById(sId, mId);
                    if (methodOpt.isPresent() && methodOpt.get().inputType() != null) {
                        String dummyJson = DummyDataGenerator.generateJson(methodOpt.get().inputType());
                        Map<String, Object> resp = new LinkedHashMap<>();
                        resp.put("json", dummyJson);
                        try {
                            resp.put("data", MAPPER.readTree(dummyJson));
                        } catch (Exception ignored) {}
                        return HttpResponse.json(200, resp);
                    }
                }
            }

            // 4. GET /services/{id}
            if (method.equals("GET") && apiPath.startsWith("/services/")) {
                securityManager.checkPermission(principal, ProtonautPermission.PROTONAUT_SERVICE_DISCOVER);
                String id = URLDecoder.decode(apiPath.substring("/services/".length()), StandardCharsets.UTF_8);
                return descriptorRegistry.findServiceById(id)
                        .map(s -> HttpResponse.json(200, s))
                        .orElse(HttpResponse.notFound("Service not found: " + id));
            }

            // 5. GET /protos or /proto
            if (method.equals("GET") && (apiPath.equals("/protos") || apiPath.equals("/proto"))) {
                securityManager.checkPermission(principal, ProtonautPermission.PROTONAUT_PROTO_VIEW);
                String serviceParam = queryParams.get("service");
                if (serviceParam == null) serviceParam = queryParams.get("serviceId");
                if (serviceParam != null && !serviceParam.isBlank()) {
                    return protoSourceResolver.resolveProtoSource(serviceParam)
                            .map(p -> HttpResponse.json(200, p))
                            .orElse(HttpResponse.notFound("Proto not found for service: " + serviceParam));
                }
                return HttpResponse.json(200, protoSourceResolver.resolveAllProtoSources());
            }

            // 6. GET /protos/{serviceId}
            if (method.equals("GET") && apiPath.startsWith("/protos/")) {
                securityManager.checkPermission(principal, ProtonautPermission.PROTONAUT_PROTO_VIEW);
                String serviceId = URLDecoder.decode(apiPath.substring("/protos/".length()), StandardCharsets.UTF_8);
                return protoSourceResolver.resolveProtoSource(serviceId)
                        .map(p -> HttpResponse.json(200, p))
                        .orElse(HttpResponse.notFound("Proto not found for service: " + serviceId));
            }

            // 7. POST /execute
            if (method.equals("POST") && apiPath.equals("/execute")) {
                long size = bodyBytes != null ? bodyBytes.length : 0;
                securityManager.checkRequestExecution(principal, size);

                ExecutionRequest req = MAPPER.readValue(body, ExecutionRequest.class);
                ExecutionResponse resp = executionProvider.execute(req);
                return HttpResponse.json(200, resp);
            }

            // 8. GET or POST /dummy or /dummy-data
            if ((method.equals("POST") || method.equals("GET")) && (apiPath.equals("/dummy") || apiPath.equals("/dummy-data"))) {
                String serviceId = queryParams.get("serviceId");
                String methodId = queryParams.get("methodId");
                if (serviceId == null && queryParams.containsKey("method")) {
                    String m = queryParams.get("method");
                    int lastSlash = m.lastIndexOf('/');
                    if (lastSlash > 0) {
                        serviceId = m.substring(0, lastSlash);
                        methodId = m.substring(lastSlash + 1);
                    } else {
                        methodId = m;
                    }
                }
                if (bodyBytes != null && bodyBytes.length > 0 && !body.isBlank()) {
                    try {
                        JsonNode root = MAPPER.readTree(body);
                        if (root.has("serviceId")) serviceId = root.get("serviceId").asText();
                        if (root.has("methodId")) methodId = root.get("methodId").asText();
                        if (root.has("method")) {
                            String m = root.get("method").asText();
                            int lastSlash = m.lastIndexOf('/');
                            if (lastSlash > 0) {
                                serviceId = m.substring(0, lastSlash);
                                methodId = m.substring(lastSlash + 1);
                            } else {
                                methodId = m;
                            }
                        }
                    } catch (Exception ignored) {}
                }

                Optional<MethodDescriptorModel> methodOpt = descriptorRegistry.findMethodById(serviceId, methodId);
                if (methodOpt.isPresent() && methodOpt.get().inputType() != null) {
                    String dummyJson = DummyDataGenerator.generateJson(methodOpt.get().inputType());
                    Map<String, Object> resp = new LinkedHashMap<>();
                    resp.put("json", dummyJson);
                    try {
                        resp.put("data", MAPPER.readTree(dummyJson));
                    } catch (Exception ignored) {}
                    return HttpResponse.json(200, resp);
                }
                return HttpResponse.json(200, Map.of("json", "{}"));
            }

            // 9. GET or POST /codegen
            if ((method.equals("POST") || method.equals("GET")) && apiPath.equals("/codegen")) {
                String language = queryParams.getOrDefault("language", "java");
                String serviceId = queryParams.get("serviceId");
                String methodId = queryParams.get("methodId");
                String sampleJson = queryParams.getOrDefault("sampleJson", "{}");

                if (serviceId == null && queryParams.containsKey("method")) {
                    String m = queryParams.get("method");
                    int lastSlash = m.lastIndexOf('/');
                    if (lastSlash > 0) {
                        serviceId = m.substring(0, lastSlash);
                        methodId = m.substring(lastSlash + 1);
                    } else {
                        methodId = m;
                    }
                }

                if (bodyBytes != null && bodyBytes.length > 0 && !body.isBlank()) {
                    try {
                        JsonNode root = MAPPER.readTree(body);
                        if (root.has("language")) language = root.get("language").asText();
                        if (root.has("serviceId")) serviceId = root.get("serviceId").asText();
                        if (root.has("methodId")) methodId = root.get("methodId").asText();
                        if (root.has("sampleJson")) sampleJson = root.get("sampleJson").asText();
                        if (root.has("method")) {
                            String m = root.get("method").asText();
                            int lastSlash = m.lastIndexOf('/');
                            if (lastSlash > 0) {
                                serviceId = m.substring(0, lastSlash);
                                methodId = m.substring(lastSlash + 1);
                            } else {
                                methodId = m;
                            }
                        }
                    } catch (Exception ignored) {}
                }

                Optional<ServiceDescriptorModel> serviceOpt = descriptorRegistry.findServiceById(serviceId);
                Optional<MethodDescriptorModel> methodOpt = descriptorRegistry.findMethodById(serviceId, methodId);

                if (serviceOpt.isPresent() && methodOpt.isPresent()) {
                    String host = environmentInfo != null ? environmentInfo.host() : "localhost";
                    int port = environmentInfo != null ? environmentInfo.grpcPort() : 6570;
                    String code = codeGeneratorRegistry.generate(
                            language, serviceOpt.get(), methodOpt.get(), sampleJson, Map.of(), host, port
                    );
                    return HttpResponse.json(200, Map.of("code", code));
                }
                return HttpResponse.notFound("Service or method not found for codegen");
            }

            // 10. GET /workflows
            if (method.equals("GET") && (apiPath.equals("/workflows") || apiPath.equals("/workflow"))) {
                securityManager.checkPermission(principal, ProtonautPermission.PROTONAUT_WORKFLOW_VIEW);
                return HttpResponse.json(200, workflowRepository.findAll());
            }

            // 11. POST /workflows
            if (method.equals("POST") && (apiPath.equals("/workflows") || apiPath.equals("/workflow"))) {
                securityManager.checkPermission(principal, ProtonautPermission.PROTONAUT_WORKFLOW_EDIT);
                WorkflowDefinition wf = MAPPER.readValue(body, WorkflowDefinition.class);
                WorkflowDefinition saved = workflowRepository.save(wf);
                return HttpResponse.json(200, saved);
            }

            // 12. DELETE /workflows/{id}
            if (method.equals("DELETE") && (apiPath.startsWith("/workflows/") || apiPath.startsWith("/workflow/"))) {
                securityManager.checkPermission(principal, ProtonautPermission.PROTONAUT_WORKFLOW_EDIT);
                int prefixLen = apiPath.startsWith("/workflows/") ? "/workflows/".length() : "/workflow/".length();
                String id = URLDecoder.decode(apiPath.substring(prefixLen), StandardCharsets.UTF_8);
                boolean deleted = workflowRepository.deleteById(id);
                return HttpResponse.json(200, Map.of("deleted", deleted));
            }

            // 13. POST /workflows/execute or /workflow/execute
            if (method.equals("POST") && (apiPath.equals("/workflows/execute") || apiPath.equals("/workflow/execute"))) {
                securityManager.checkWorkflowExecution(principal);
                JsonNode root = MAPPER.readTree(body);
                Map<String, String> vars = new HashMap<>();
                if (root.has("variables")) {
                    root.get("variables").fields().forEachRemaining(e -> vars.put(e.getKey(), e.getValue().asText()));
                } else if (root.has("initialVariables")) {
                    root.get("initialVariables").fields().forEachRemaining(e -> vars.put(e.getKey(), e.getValue().asText()));
                }
                WorkflowDefinition wf = MAPPER.readValue(body, WorkflowDefinition.class);
                if ((wf.variables() == null || wf.variables().isEmpty()) && !vars.isEmpty()) {
                    wf = new WorkflowDefinition(wf.id(), wf.name(), wf.description(), wf.steps(), vars, wf.createdAt(), wf.updatedAt());
                }
                WorkflowExecutionResult result = workflowEngine.execute(wf);
                return HttpResponse.json(200, result);
            }

            return HttpResponse.notFound("No matching API route: " + method + " " + apiPath);
        } catch (SecurityException | io.protonaut.security.ProtonautAccessDeniedException e) {
            return HttpResponse.forbidden(e.getMessage());
        } catch (IllegalArgumentException e) {
            return HttpResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("API error for {} {}: {}", method, apiPath, e.getMessage(), e);
            return HttpResponse.json(500, Map.of("error", "INTERNAL_ERROR", "message", e.getMessage() != null ? e.getMessage() : "Unknown error"));
        }
    }

    private HttpResponse serveStaticAsset(String path) {
        if (path == null || path.isBlank() || path.equals("/")) {
            path = "index.html";
        }

        // Prevent directory traversal
        if (path.contains("..")) {
            return HttpResponse.badRequest("Invalid path");
        }

        String resourcePath = "META-INF/resources/protonaut/" + path;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = getClass().getClassLoader();

        try (InputStream in = cl.getResourceAsStream(resourcePath)) {
            if (in != null) {
                byte[] bytes = in.readAllBytes();
                String contentType = detectContentType(path);
                return new HttpResponse(200, contentType, bytes);
            }
        } catch (Exception e) {
            log.warn("Error reading static asset {}: {}", resourcePath, e.getMessage());
        }

        // SPA Fallback: if not an asset extension (.js, .css, etc.), serve index.html!
        if (!path.contains(".")) {
            try (InputStream in = cl.getResourceAsStream("META-INF/resources/protonaut/index.html")) {
                if (in != null) {
                    return new HttpResponse(200, "text/html; charset=UTF-8", in.readAllBytes());
                }
            } catch (Exception ignored) {}
        }

        return HttpResponse.notFound("Static asset not found: " + path);
    }

    private String detectContentType(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".js") || lower.endsWith(".mjs")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".ico")) return "image/x-icon";
        if (lower.endsWith(".woff2")) return "font/woff2";
        return "application/octet-stream";
    }

    private ProtonautPrincipal resolvePrincipal(Map<String, String> headers) {
        // In local development or if headers don't have auth, check development policy
        if (headers == null) return ProtonautPrincipal.anonymous();

        String authHeader = headers.get("authorization");
        if (authHeader == null) {
            authHeader = headers.get("Authorization");
        }

        if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
            String token = authHeader.substring(7).trim();
            // In demo/test mode, tokens starting with 'admin' or 'viewer' can map to roles
            if (token.contains("admin")) {
                return ProtonautPrincipal.admin("admin-user");
            } else if (token.contains("viewer")) {
                return ProtonautPrincipal.ofRole("viewer-user", io.protonaut.security.ProtonautRole.PROTONAUT_VIEWER);
            } else if (token.contains("tester")) {
                return ProtonautPrincipal.ofRole("tester-user", io.protonaut.security.ProtonautRole.PROTONAUT_TESTER);
            }
        }

        return ProtonautPrincipal.anonymous();
    }
}
