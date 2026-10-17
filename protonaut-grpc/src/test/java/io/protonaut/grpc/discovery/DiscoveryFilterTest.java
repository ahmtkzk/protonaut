package io.protonaut.grpc.discovery;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DiscoveryFilterTest {

    @Test
    void testDefaultFiltersReflectionAndHealth() {
        DiscoveryFilter filter = DiscoveryFilter.defaultFilter();

        assertThat(filter.isServiceAllowed("grpc.reflection.v1alpha.ServerReflection", "grpc.reflection.v1alpha", "ServerReflection")).isFalse();
        assertThat(filter.isServiceAllowed("grpc.health.v1.Health", "grpc.health.v1", "Health")).isFalse();
        assertThat(filter.isServiceAllowed("com.example.UserService", "com.example", "UserService")).isTrue();
    }

    @Test
    void testIncludeAndExcludePackages() {
        DiscoveryFilter filter = new DiscoveryFilter(
                List.of(),
                List.of("com.internal.*"),
                List.of("com.*"),
                List.of(),
                false
        );

        assertThat(filter.isServiceAllowed("com.example.OrderService", "com.example", "OrderService")).isTrue();
        assertThat(filter.isServiceAllowed("com.internal.SecretService", "com.internal", "SecretService")).isFalse();
        assertThat(filter.isServiceAllowed("org.other.OtherService", "org.other", "OtherService")).isFalse();
    }
}
