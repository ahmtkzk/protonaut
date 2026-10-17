package io.protonaut.core.models;

import java.util.List;

public record MessageDescriptorModel(
        String name,
        String fullName,
        List<FieldDescriptorModel> fields,
        List<EnumDescriptorModel> enums,
        List<MessageDescriptorModel> nestedMessages,
        List<String> oneofs,
        String description,
        String protoFile
) {
    public MessageDescriptorModel {
        fields = fields != null ? List.copyOf(fields) : List.of();
        enums = enums != null ? List.copyOf(enums) : List.of();
        nestedMessages = nestedMessages != null ? List.copyOf(nestedMessages) : List.of();
        oneofs = oneofs != null ? List.copyOf(oneofs) : List.of();
    }

    public FieldDescriptorModel findFieldByName(String fieldName) {
        if (fieldName == null) return null;
        for (FieldDescriptorModel f : fields) {
            if (f.name().equalsIgnoreCase(fieldName) || f.jsonName().equalsIgnoreCase(fieldName)) {
                return f;
            }
        }
        return null;
    }
}
