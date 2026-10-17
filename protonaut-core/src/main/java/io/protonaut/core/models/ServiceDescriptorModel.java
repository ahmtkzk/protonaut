package io.protonaut.core.models;

import java.util.List;

public record ServiceDescriptorModel(
        String id,
        String name,
        String fullName,
        String packageName,
        List<MethodDescriptorModel> methods,
        String description,
        String protoFile
) {
    public ServiceDescriptorModel {
        methods = methods != null ? List.copyOf(methods) : List.of();
    }

    public MethodDescriptorModel findMethodByName(String methodName) {
        if (methodName == null) return null;
        for (MethodDescriptorModel m : methods) {
            if (m.name().equalsIgnoreCase(methodName) || m.id().equalsIgnoreCase(methodName)) {
                return m;
            }
        }
        return null;
    }
}
