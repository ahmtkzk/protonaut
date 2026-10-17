package io.protonaut.codegen;

import io.protonaut.codegen.generators.*;
import io.protonaut.core.models.MethodDescriptorModel;
import io.protonaut.core.models.ServiceDescriptorModel;
import io.protonaut.core.spi.ClientCodeGenerator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CodeGeneratorRegistry {
    private final Map<String, ClientCodeGenerator> generators = new ConcurrentHashMap<>();

    public CodeGeneratorRegistry() {
        register(new JavaClientCodeGenerator());
        register(new GoClientCodeGenerator());
        register(new TypeScriptClientCodeGenerator());
        register(new PythonClientCodeGenerator());
        register(new CSharpClientCodeGenerator());
    }

    public void register(ClientCodeGenerator generator) {
        if (generator != null && generator.getLanguage() != null) {
            generators.put(generator.getLanguage().toLowerCase(), generator);
        }
    }

    public Optional<ClientCodeGenerator> getGenerator(String language) {
        if (language == null) return Optional.empty();
        return Optional.ofNullable(generators.get(language.toLowerCase()));
    }

    public List<ClientCodeGenerator> getAllGenerators() {
        return List.copyOf(generators.values());
    }

    public String generate(
            String language,
            ServiceDescriptorModel service,
            MethodDescriptorModel method,
            String sampleJson,
            Map<String, String> metadata,
            String host,
            int port
    ) {
        return getGenerator(language)
                .map(g -> g.generate(service, method, sampleJson, metadata, host, port))
                .orElse("// Unsupported language: " + language);
    }
}
