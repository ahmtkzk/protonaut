package io.protonaut.autoconfigure;

import io.protonaut.core.spi.DescriptorRegistry;
import io.protonaut.core.spi.WorkflowEngine;
import io.protonaut.web.netty.ProtonautServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ProtonautAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ProtonautAutoConfiguration.class));

    @Test
    void testDefaultAutoConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ProtonautProperties.class);
            assertThat(context).hasSingleBean(DescriptorRegistry.class);
            assertThat(context).hasSingleBean(WorkflowEngine.class);
            assertThat(context).hasSingleBean(ProtonautServer.class);
            assertThat(context).hasSingleBean(ProtonautLifecycleManager.class);

            ProtonautProperties props = context.getBean(ProtonautProperties.class);
            assertThat(props.getGrpc().getPort()).isEqualTo(6570);
            assertThat(props.getRest().getPort()).isEqualTo(8590);
            assertThat(props.getRest().isEnabled()).isTrue();
        });
    }

    @Test
    void testDisabledAutoConfiguration() {
        contextRunner.withPropertyValues("protonaut.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ProtonautProperties.class);
                    assertThat(context).doesNotHaveBean(ProtonautServer.class);
                });
    }

    @Test
    void testCustomPropertiesBinding() {
        contextRunner.withPropertyValues(
                "protonaut.grpc.port=6580",
                "protonaut.rest.enabled=false",
                "protonaut.homepage.project-name=Custom gRPC App"
        ).run(context -> {
            ProtonautProperties props = context.getBean(ProtonautProperties.class);
            assertThat(props.getGrpc().getPort()).isEqualTo(6580);
            assertThat(props.getRest().isEnabled()).isFalse();
            assertThat(props.getHomepage().getProjectName()).isEqualTo("Custom gRPC App");

            ProtonautServer server = context.getBean(ProtonautServer.class);
            assertThat(server.getGrpcPort()).isEqualTo(6580);
            assertThat(server.isRestEnabled()).isFalse();
        });
    }
}
