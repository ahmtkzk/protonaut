package io.protonaut.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "protonaut")
public class ProtonautProperties {

    private boolean enabled = true;
    private UiProperties ui = new UiProperties();
    private GrpcProperties grpc = new GrpcProperties();
    private RestProperties rest = new RestProperties();
    private DiscoveryProperties discovery = new DiscoveryProperties();
    private ExecutionProperties execution = new ExecutionProperties();
    private WorkflowProperties workflow = new WorkflowProperties();
    private SecurityProperties security = new SecurityProperties();
    private HomepageProperties homepage = new HomepageProperties();
    private StorageProperties storage = new StorageProperties();
    private LoggingProperties logging = new LoggingProperties();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public UiProperties getUi() { return ui; }
    public void setUi(UiProperties ui) { this.ui = ui; }

    public GrpcProperties getGrpc() { return grpc; }
    public void setGrpc(GrpcProperties grpc) { this.grpc = grpc; }

    public RestProperties getRest() { return rest; }
    public void setRest(RestProperties rest) { this.rest = rest; }

    public DiscoveryProperties getDiscovery() { return discovery; }
    public void setDiscovery(DiscoveryProperties discovery) { this.discovery = discovery; }

    public ExecutionProperties getExecution() { return execution; }
    public void setExecution(ExecutionProperties execution) { this.execution = execution; }

    public WorkflowProperties getWorkflow() { return workflow; }
    public void setWorkflow(WorkflowProperties workflow) { this.workflow = workflow; }

    public SecurityProperties getSecurity() { return security; }
    public void setSecurity(SecurityProperties security) { this.security = security; }

    public HomepageProperties getHomepage() { return homepage; }
    public void setHomepage(HomepageProperties homepage) { this.homepage = homepage; }

    public StorageProperties getStorage() { return storage; }
    public void setStorage(StorageProperties storage) { this.storage = storage; }

    public LoggingProperties getLogging() { return logging; }
    public void setLogging(LoggingProperties logging) { this.logging = logging; }

    public static class UiProperties {
        private boolean enabled = true;
        private String path = "/protonaut";
        private String title = "Protonaut";
        private String theme = "system";
        private boolean showHome = true;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }

        public boolean isShowHome() { return showHome; }
        public void setShowHome(boolean showHome) { this.showHome = showHome; }
    }

    public static class GrpcProperties {
        private boolean enabled = true;
        private int port = 6570;
        private String host = "0.0.0.0";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }

        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
    }

    public static class RestProperties {
        private boolean enabled = true;
        private int port = 8590;
        private String host = "0.0.0.0";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }

        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
    }

    public static class DiscoveryProperties {
        private boolean enabled = true;
        private boolean reflectionEnabled = false;
        private List<String> includeServices = new ArrayList<>();
        private List<String> excludeServices = new ArrayList<>();
        private List<String> includePackages = new ArrayList<>();
        private List<String> excludePackages = new ArrayList<>();

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public boolean isReflectionEnabled() { return reflectionEnabled; }
        public void setReflectionEnabled(boolean reflectionEnabled) { this.reflectionEnabled = reflectionEnabled; }

        public List<String> getIncludeServices() { return includeServices; }
        public void setIncludeServices(List<String> includeServices) { this.includeServices = includeServices; }

        public List<String> getExcludeServices() { return excludeServices; }
        public void setExcludeServices(List<String> excludeServices) { this.excludeServices = excludeServices; }

        public List<String> getIncludePackages() { return includePackages; }
        public void setIncludePackages(List<String> includePackages) { this.includePackages = includePackages; }

        public List<String> getExcludePackages() { return excludePackages; }
        public void setExcludePackages(List<String> excludePackages) { this.excludePackages = excludePackages; }
    }

    public static class ExecutionProperties {
        private boolean enabled = true;
        private int timeoutSeconds = 30;
        private long maxRequestBytes = 1048576; // 1MB
        private long maxResponseBytes = 4194304; // 4MB
        private int maxConcurrentRequests = 20;
        private boolean failFastOnPortConflict = false;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public int getTimeoutSeconds() { return timeoutSeconds; }
        public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }

        public long getMaxRequestBytes() { return maxRequestBytes; }
        public void setMaxRequestBytes(long maxRequestBytes) { this.maxRequestBytes = maxRequestBytes; }

        public long getMaxResponseBytes() { return maxResponseBytes; }
        public void setMaxResponseBytes(long maxResponseBytes) { this.maxResponseBytes = maxResponseBytes; }

        public int getMaxConcurrentRequests() { return maxConcurrentRequests; }
        public void setMaxConcurrentRequests(int maxConcurrentRequests) { this.maxConcurrentRequests = maxConcurrentRequests; }

        public boolean isFailFastOnPortConflict() { return failFastOnPortConflict; }
        public void setFailFastOnPortConflict(boolean failFastOnPortConflict) { this.failFastOnPortConflict = failFastOnPortConflict; }
    }

    public static class WorkflowProperties {
        private boolean enabled = true;
        private boolean executionEnabled = true;
        private int maxSteps = 50;
        private int maxExecutionSeconds = 300;
        private int maxConcurrentRuns = 5;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public boolean isExecutionEnabled() { return executionEnabled; }
        public void setExecutionEnabled(boolean executionEnabled) { this.executionEnabled = executionEnabled; }

        public int getMaxSteps() { return maxSteps; }
        public void setMaxSteps(int maxSteps) { this.maxSteps = maxSteps; }

        public int getMaxExecutionSeconds() { return maxExecutionSeconds; }
        public void setMaxExecutionSeconds(int maxExecutionSeconds) { this.maxExecutionSeconds = maxExecutionSeconds; }

        public int getMaxConcurrentRuns() { return maxConcurrentRuns; }
        public void setMaxConcurrentRuns(int maxConcurrentRuns) { this.maxConcurrentRuns = maxConcurrentRuns; }
    }

    public static class SecurityProperties {
        private String mode = "secure";
        private boolean requireAuthentication = true;
        private boolean allowAnonymousLocalDevelopment = false;
        private boolean allowBrowserTokenStorage = false;
        private boolean maskSensitiveValues = true;

        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }

        public boolean isRequireAuthentication() { return requireAuthentication; }
        public void setRequireAuthentication(boolean requireAuthentication) { this.requireAuthentication = requireAuthentication; }

        public boolean isAllowAnonymousLocalDevelopment() { return allowAnonymousLocalDevelopment; }
        public void setAllowAnonymousLocalDevelopment(boolean allowAnonymousLocalDevelopment) { this.allowAnonymousLocalDevelopment = allowAnonymousLocalDevelopment; }

        public boolean isAllowBrowserTokenStorage() { return allowBrowserTokenStorage; }
        public void setAllowBrowserTokenStorage(boolean allowBrowserTokenStorage) { this.allowBrowserTokenStorage = allowBrowserTokenStorage; }

        public boolean isMaskSensitiveValues() { return maskSensitiveValues; }
        public void setMaskSensitiveValues(boolean maskSensitiveValues) { this.maskSensitiveValues = maskSensitiveValues; }
    }

    public static class HomepageProperties {
        private String teamName = "Platform Engineering";
        private String projectName = "Protonaut gRPC Explorer";
        private String environment = "development";
        private String description = "Internal gRPC API Explorer and Workflow Studio";
        private String version = "1.0.0";

        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }

        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }

        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }

    public static class StorageProperties {
        private boolean allowBrowserPersistence = true;

        public boolean isAllowBrowserPersistence() { return allowBrowserPersistence; }
        public void setAllowBrowserPersistence(boolean allowBrowserPersistence) { this.allowBrowserPersistence = allowBrowserPersistence; }
    }

    public static class LoggingProperties {
        private boolean enabled = true;
        private boolean logRequestMetadata = false;
        private boolean logResponseBodies = false;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public boolean isLogRequestMetadata() { return logRequestMetadata; }
        public void setLogRequestMetadata(boolean logRequestMetadata) { this.logRequestMetadata = logRequestMetadata; }

        public boolean isLogResponseBodies() { return logResponseBodies; }
        public void setLogResponseBodies(boolean logResponseBodies) { this.logResponseBodies = logResponseBodies; }
    }
}
