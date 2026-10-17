package io.protonaut.core.models;

import java.util.List;

public record EnumValueModel(
        String name,
        int number,
        String description
) {}
