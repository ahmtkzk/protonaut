package io.protonaut.core.spi;

import io.protonaut.core.models.MethodDescriptorModel;
import io.protonaut.core.models.ServiceDescriptorModel;

import java.util.Map;

public interface ClientCodeGenerator {
    /**
     * Language identifier (e.g. "java", "go", "typescript", "python", "csharp").
     */
    String getLanguage();

    /**
     * Display name for UI.
     */
    String getDisplayName();

    /**
     * Generates a fully working client snippet for the given service and method.
     */
    String generate(
            ServiceDescriptorModel service,
            MethodDescriptorModel method,
            String sampleJson,
            Map<String, String> metadata,
            String host,
            int port
    );
}
