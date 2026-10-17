package io.protonaut.core.metadata;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MetadataGeneratorTest {

    @Test
    void testResolveTokens() {
        String template = "bearer-{{uuid}}-time-{{timestamp}}-rand-{{randomString}}";
        String resolved = MetadataGenerator.resolveValue(template);

        assertThat(resolved).doesNotContain("{{uuid}}");
        assertThat(resolved).doesNotContain("{{timestamp}}");
        assertThat(resolved).doesNotContain("{{randomString}}");
        assertThat(resolved).startsWith("bearer-");
    }

    @Test
    void testResolveMetadataMap() {
        Map<String, String> input = Map.of(
                "x-request-id", "{{uuid}}",
                "x-sequence", "{{sequence}}",
                "invalid key!", "should-be-dropped"
        );

        Map<String, String> resolved = MetadataGenerator.resolveMetadata(input);

        assertThat(resolved).containsKey("x-request-id");
        assertThat(resolved).containsKey("x-sequence");
        assertThat(resolved).doesNotContainKey("invalid key!");
        assertThat(resolved.get("x-request-id")).hasSize(36);
    }
}
