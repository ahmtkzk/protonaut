package io.protonaut.core.models;

import java.util.List;

public record EnumDescriptorModel(
        String name,
        String fullName,
        List<EnumValueModel> values,
        String description
) {
    public EnumDescriptorModel {
        values = values != null ? List.copyOf(values) : List.of();
    }
}
