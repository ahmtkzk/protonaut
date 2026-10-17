package io.protonaut.core.models;

public record FieldDescriptorModel(
        String name,
        String jsonName,
        int number,
        FieldValueType type,
        String typeName,
        boolean repeated,
        boolean map,
        boolean optional,
        boolean required,
        Integer oneofIndex,
        String oneofName,
        String description,
        Object defaultValue,
        FieldValueType mapKeyType,
        FieldValueType mapValueType,
        String mapValueTypeName,
        MessageDescriptorModel nestedType,
        EnumDescriptorModel enumType
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String jsonName;
        private int number;
        private FieldValueType type;
        private String typeName;
        private boolean repeated;
        private boolean map;
        private boolean optional;
        private boolean required;
        private Integer oneofIndex;
        private String oneofName;
        private String description;
        private Object defaultValue;
        private FieldValueType mapKeyType;
        private FieldValueType mapValueType;
        private String mapValueTypeName;
        private MessageDescriptorModel nestedType;
        private EnumDescriptorModel enumType;

        public Builder name(String name) { this.name = name; return this; }
        public Builder jsonName(String jsonName) { this.jsonName = jsonName; return this; }
        public Builder number(int number) { this.number = number; return this; }
        public Builder type(FieldValueType type) { this.type = type; return this; }
        public Builder typeName(String typeName) { this.typeName = typeName; return this; }
        public Builder repeated(boolean repeated) { this.repeated = repeated; return this; }
        public Builder map(boolean map) { this.map = map; return this; }
        public Builder optional(boolean optional) { this.optional = optional; return this; }
        public Builder required(boolean required) { this.required = required; return this; }
        public Builder oneofIndex(Integer oneofIndex) { this.oneofIndex = oneofIndex; return this; }
        public Builder oneofName(String oneofName) { this.oneofName = oneofName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder defaultValue(Object defaultValue) { this.defaultValue = defaultValue; return this; }
        public Builder mapKeyType(FieldValueType mapKeyType) { this.mapKeyType = mapKeyType; return this; }
        public Builder mapValueType(FieldValueType mapValueType) { this.mapValueType = mapValueType; return this; }
        public Builder mapValueTypeName(String mapValueTypeName) { this.mapValueTypeName = mapValueTypeName; return this; }
        public Builder nestedType(MessageDescriptorModel nestedType) { this.nestedType = nestedType; return this; }
        public Builder enumType(EnumDescriptorModel enumType) { this.enumType = enumType; return this; }

        public FieldDescriptorModel build() {
            return new FieldDescriptorModel(
                    name, jsonName != null ? jsonName : name, number,
                    type, typeName, repeated, map, optional, required,
                    oneofIndex, oneofName, description, defaultValue,
                    mapKeyType, mapValueType, mapValueTypeName, nestedType, enumType
            );
        }
    }
}
