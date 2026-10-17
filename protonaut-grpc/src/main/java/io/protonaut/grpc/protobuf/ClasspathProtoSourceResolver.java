package io.protonaut.grpc.protobuf;

import com.google.protobuf.Descriptors;
import io.protonaut.core.models.ProtoSourceModel;
import io.protonaut.core.spi.ProtoSourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClasspathProtoSourceResolver implements ProtoSourceResolver {
    private static final Logger log = LoggerFactory.getLogger(ClasspathProtoSourceResolver.class);

    private final Map<String, Descriptors.ServiceDescriptor> serviceDescriptors = new ConcurrentHashMap<>();
    private final ClassLoader classLoader;

    public ClasspathProtoSourceResolver() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ClasspathProtoSourceResolver(ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : getClass().getClassLoader();
    }

    public void registerService(Descriptors.ServiceDescriptor descriptor) {
        if (descriptor != null) {
            serviceDescriptors.put(descriptor.getFullName(), descriptor);
            serviceDescriptors.put(descriptor.getName(), descriptor);
        }
    }

    @Override
    public Optional<ProtoSourceModel> resolveProtoSource(String serviceId) {
        if (serviceId == null) return Optional.empty();

        Descriptors.ServiceDescriptor desc = serviceDescriptors.get(serviceId);
        if (desc == null) {
            // Find by matching name ignoring case
            desc = serviceDescriptors.values().stream()
                    .filter(s -> s.getFullName().equalsIgnoreCase(serviceId) || s.getName().equalsIgnoreCase(serviceId))
                    .findFirst()
                    .orElse(null);
        }

        if (desc == null) {
            return Optional.empty();
        }

        String protoFileName = desc.getFile().getName();

        // 1. Try finding original proto file on classpath
        Optional<String> originalSource = findOriginalProtoFile(protoFileName, desc.getFile().getPackage());
        String syntax = desc.getFile().toProto().hasSyntax() ? desc.getFile().toProto().getSyntax() : "proto3";
        if (originalSource.isPresent()) {
            log.debug("Found original .proto source for {}: {}", serviceId, protoFileName);
            return Optional.of(ProtoSourceModel.original(
                    protoFileName,
                    desc.getFile().getPackage(),
                    originalSource.get(),
                    syntax
            ));
        }

        // 2. Fall back to clean reconstruction
        log.debug("Reconstructing .proto source for {}: {}", serviceId, protoFileName);
        return Optional.of(ProtoReconstructor.reconstructServiceProto(desc));
    }

    @Override
    public List<ProtoSourceModel> resolveAllProtoSources() {
        Set<String> processedFiles = new HashSet<>();
        List<ProtoSourceModel> results = new ArrayList<>();

        for (Descriptors.ServiceDescriptor desc : serviceDescriptors.values()) {
            String protoFile = desc.getFile().getName();
            if (processedFiles.add(protoFile)) {
                resolveProtoSource(desc.getFullName()).ifPresent(results::add);
            }
        }

        return results;
    }

    private Optional<String> findOriginalProtoFile(String fileName, String packageName) {
        List<String> candidates = new ArrayList<>();
        candidates.add(fileName);
        candidates.add("/" + fileName);
        candidates.add("proto/" + fileName);
        candidates.add("/proto/" + fileName);
        candidates.add("META-INF/proto/" + fileName);
        candidates.add("/META-INF/proto/" + fileName);

        if (packageName != null && !packageName.isEmpty()) {
            String packagePath = packageName.replace('.', '/');
            candidates.add(packagePath + "/" + fileName);
            candidates.add("/" + packagePath + "/" + fileName);
            candidates.add("proto/" + packagePath + "/" + fileName);
        }

        for (String path : candidates) {
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;
            try (InputStream in = classLoader.getResourceAsStream(cleanPath)) {
                if (in != null) {
                    byte[] bytes = in.readAllBytes();
                    return Optional.of(new String(bytes, StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                log.trace("Error attempting to read proto file at {}: {}", cleanPath, e.getMessage());
            }
        }

        return Optional.empty();
    }
}
