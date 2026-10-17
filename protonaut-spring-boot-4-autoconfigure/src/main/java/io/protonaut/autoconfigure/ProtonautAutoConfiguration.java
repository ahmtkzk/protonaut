package io.protonaut.autoconfigure;

import io.grpc.Channel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.protonaut.codegen.CodeGeneratorRegistry;
import io.protonaut.core.models.EnvironmentInfo;
import io.protonaut.core.registry.DefaultDescriptorRegistry;
import io.protonaut.core.spi.DescriptorRegistry;
import io.protonaut.core.spi.ProtoSourceResolver;
import io.protonaut.core.spi.WorkflowEngine;
import io.protonaut.core.spi.WorkflowRepository;
import io.protonaut.grpc.execution.DynamicGrpcExecutor;
import io.protonaut.grpc.protobuf.ClasspathProtoSourceResolver;
import io.protonaut.security.ProtonautSecurityManager;
import io.protonaut.security.SecurityConfigModel;
import io.protonaut.web.dispatch.ProtonautRestDispatcher;
import io.protonaut.web.netty.ProtonautServer;
import io.protonaut.workflow.engine.WorkflowEngineImpl;
import io.protonaut.workflow.repository.InMemoryWorkflowRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.UUID;

@AutoConfiguration
@ConditionalOnProperty(prefix = "protonaut", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ProtonautProperties.class)
public class ProtonautAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DescriptorRegistry descriptorRegistry() {
        return new DefaultDescriptorRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClasspathProtoSourceResolver protoSourceResolver() {
        return new ClasspathProtoSourceResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public CodeGeneratorRegistry codeGeneratorRegistry() {
        return new CodeGeneratorRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public WorkflowRepository workflowRepository() {
        return new InMemoryWorkflowRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicGrpcExecutor dynamicGrpcExecutor() {
        // Default in-process channel for execution
        Channel fallbackChannel = InProcessChannelBuilder.forName("protonaut-fallback-" + UUID.randomUUID())
                .directExecutor()
                .build();
        return new DynamicGrpcExecutor(fallbackChannel);
    }

    @Bean
    @ConditionalOnMissingBean
    public WorkflowEngine workflowEngine(DynamicGrpcExecutor dynamicGrpcExecutor) {
        return new WorkflowEngineImpl(dynamicGrpcExecutor);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProtonautSecurityManager protonautSecurityManager(ProtonautProperties properties) {
        ProtonautProperties.SecurityProperties sec = properties.getSecurity();
        ProtonautProperties.ExecutionProperties exec = properties.getExecution();
        ProtonautProperties.WorkflowProperties wf = properties.getWorkflow();

        SecurityConfigModel config = new SecurityConfigModel(
                sec.getMode(),
                sec.isRequireAuthentication(),
                sec.isAllowAnonymousLocalDevelopment(),
                sec.isAllowBrowserTokenStorage(),
                sec.isMaskSensitiveValues(),
                exec.isEnabled(),
                wf.isExecutionEnabled(),
                exec.getMaxRequestBytes(),
                exec.getMaxResponseBytes(),
                exec.getMaxConcurrentRequests()
        );
        return new ProtonautSecurityManager(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public EnvironmentInfo environmentInfo(ProtonautProperties properties) {
        return new EnvironmentInfo(
                properties.getHomepage().getProjectName(),
                properties.getHomepage().getTeamName(),
                properties.getHomepage().getEnvironment(),
                properties.getHomepage().getVersion(),
                properties.getHomepage().getDescription(),
                properties.getGrpc().getPort(),
                properties.getRest().getPort(),
                properties.getRest().isEnabled(),
                properties.getGrpc().getHost(),
                "UP",
                Map.of()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ProtonautRestDispatcher protonautRestDispatcher(
            DescriptorRegistry descriptorRegistry,
            DynamicGrpcExecutor dynamicGrpcExecutor,
            ClasspathProtoSourceResolver protoSourceResolver,
            WorkflowEngine workflowEngine,
            WorkflowRepository workflowRepository,
            CodeGeneratorRegistry codeGeneratorRegistry,
            ProtonautSecurityManager protonautSecurityManager,
            EnvironmentInfo environmentInfo
    ) {
        return new ProtonautRestDispatcher(
                descriptorRegistry,
                dynamicGrpcExecutor,
                protoSourceResolver,
                workflowEngine,
                workflowRepository,
                codeGeneratorRegistry,
                protonautSecurityManager,
                environmentInfo
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ProtonautServer protonautServer(
            ProtonautProperties properties,
            ProtonautRestDispatcher dispatcher
    ) {
        return new ProtonautServer(
                properties.getGrpc().getHost(),
                properties.getGrpc().getPort(),
                properties.getRest().getPort(),
                properties.getRest().isEnabled(),
                properties.getExecution().isFailFastOnPortConflict(),
                dispatcher
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ProtonautLifecycleManager protonautLifecycleManager(
            ApplicationContext applicationContext,
            ProtonautProperties properties,
            DescriptorRegistry descriptorRegistry,
            DynamicGrpcExecutor dynamicGrpcExecutor,
            ClasspathProtoSourceResolver protoSourceResolver,
            ProtonautServer protonautServer
    ) {
        return new ProtonautLifecycleManager(
                applicationContext,
                properties,
                descriptorRegistry,
                dynamicGrpcExecutor,
                protoSourceResolver,
                protonautServer
        );
    }
}
