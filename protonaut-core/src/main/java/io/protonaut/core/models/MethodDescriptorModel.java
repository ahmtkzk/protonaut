package io.protonaut.core.models;

public record MethodDescriptorModel(
        String id,
        String name,
        String fullMethodName,
        String serviceName,
        String packageName,
        MethodType type,
        MessageDescriptorModel inputType,
        MessageDescriptorModel outputType,
        String description,
        String protoFile
) {}
