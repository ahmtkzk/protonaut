package io.protonaut.grpc.descriptors;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import io.protonaut.core.models.*;

import java.util.*;

public class ProtobufDescriptorConverter {

    public static ServiceDescriptorModel toServiceModel(Descriptors.ServiceDescriptor serviceDesc) {
        String fullName = serviceDesc.getFullName();
        String packageName = serviceDesc.getFile().getPackage();
        String name = serviceDesc.getName();
        String protoFile = serviceDesc.getFile().getName();

        List<MethodDescriptorModel> methods = new ArrayList<>();
        for (Descriptors.MethodDescriptor methodDesc : serviceDesc.getMethods()) {
            methods.add(toMethodModel(methodDesc));
        }

        String comment = getServiceComment(serviceDesc);

        return new ServiceDescriptorModel(
                fullName,
                name,
                fullName,
                packageName,
                methods,
                comment,
                protoFile
        );
    }

    public static MethodDescriptorModel toMethodModel(Descriptors.MethodDescriptor methodDesc) {
        String name = methodDesc.getName();
        String fullMethodName = methodDesc.getService().getFullName() + "/" + name;
        String serviceName = methodDesc.getService().getName();
        String packageName = methodDesc.getFile().getPackage();
        String protoFile = methodDesc.getFile().getName();

        MethodType methodType;
        if (methodDesc.isClientStreaming() && methodDesc.isServerStreaming()) {
            methodType = MethodType.BIDI_STREAMING;
        } else if (methodDesc.isClientStreaming()) {
            methodType = MethodType.CLIENT_STREAMING;
        } else if (methodDesc.isServerStreaming()) {
            methodType = MethodType.SERVER_STREAMING;
        } else {
            methodType = MethodType.UNARY;
        }

        MessageDescriptorModel inputType = toMessageModel(methodDesc.getInputType(), new HashSet<>());
        MessageDescriptorModel outputType = toMessageModel(methodDesc.getOutputType(), new HashSet<>());

        return new MethodDescriptorModel(
                fullMethodName,
                name,
                fullMethodName,
                serviceName,
                packageName,
                methodType,
                inputType,
                outputType,
                null,
                protoFile
        );
    }

    public static MessageDescriptorModel toMessageModel(Descriptors.Descriptor descriptor, Set<String> visited) {
        if (descriptor == null) return null;

        String fullName = descriptor.getFullName();
        String name = descriptor.getName();
        String protoFile = descriptor.getFile().getName();

        if (visited.contains(fullName)) {
            // Avoid circular infinite recursion in schemas
            return new MessageDescriptorModel(name, fullName, List.of(), List.of(), List.of(), List.of(), "Recursive reference", protoFile);
        }

        Set<String> currentVisited = new HashSet<>(visited);
        currentVisited.add(fullName);

        List<FieldDescriptorModel> fields = new ArrayList<>();
        for (Descriptors.FieldDescriptor fieldDesc : descriptor.getFields()) {
            fields.add(toFieldModel(fieldDesc, currentVisited));
        }

        List<EnumDescriptorModel> enums = new ArrayList<>();
        for (Descriptors.EnumDescriptor enumDesc : descriptor.getEnumTypes()) {
            enums.add(toEnumModel(enumDesc));
        }

        List<MessageDescriptorModel> nested = new ArrayList<>();
        for (Descriptors.Descriptor nestedDesc : descriptor.getNestedTypes()) {
            if (!nestedDesc.getOptions().getMapEntry()) {
                nested.add(toMessageModel(nestedDesc, currentVisited));
            }
        }

        List<String> oneofs = descriptor.getOneofs().stream()
                .map(Descriptors.OneofDescriptor::getName)
                .toList();

        return new MessageDescriptorModel(
                name,
                fullName,
                fields,
                enums,
                nested,
                oneofs,
                null,
                protoFile
        );
    }

    public static FieldDescriptorModel toFieldModel(Descriptors.FieldDescriptor fieldDesc, Set<String> visited) {
        String name = fieldDesc.getName();
        String jsonName = fieldDesc.getJsonName();
        int number = fieldDesc.getNumber();
        boolean isRepeated = fieldDesc.isRepeated();
        boolean isMap = fieldDesc.isMapField();
        boolean isOptional = fieldDesc.isOptional() && !isRepeated;
        boolean isRequired = fieldDesc.isRequired();

        Integer oneofIndex = fieldDesc.getRealContainingOneof() != null
                ? fieldDesc.getRealContainingOneof().getIndex()
                : null;
        String oneofName = fieldDesc.getRealContainingOneof() != null
                ? fieldDesc.getRealContainingOneof().getName()
                : null;

        FieldValueType type = mapFieldType(fieldDesc.getType());
        String typeName = null;
        MessageDescriptorModel nestedType = null;
        EnumDescriptorModel enumType = null;
        FieldValueType mapKeyType = null;
        FieldValueType mapValueType = null;
        String mapValueTypeName = null;

        if (isMap) {
            Descriptors.Descriptor mapEntryDesc = fieldDesc.getMessageType();
            Descriptors.FieldDescriptor keyField = mapEntryDesc.findFieldByNumber(1);
            Descriptors.FieldDescriptor valueField = mapEntryDesc.findFieldByNumber(2);
            if (keyField != null) {
                mapKeyType = mapFieldType(keyField.getType());
            }
            if (valueField != null) {
                mapValueType = mapFieldType(valueField.getType());
                if (valueField.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                    mapValueTypeName = valueField.getMessageType().getFullName();
                    nestedType = toMessageModel(valueField.getMessageType(), visited);
                } else if (valueField.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
                    mapValueTypeName = valueField.getEnumType().getFullName();
                    enumType = toEnumModel(valueField.getEnumType());
                }
            }
        } else if (fieldDesc.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
            typeName = fieldDesc.getMessageType().getFullName();
            nestedType = toMessageModel(fieldDesc.getMessageType(), visited);
        } else if (fieldDesc.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
            typeName = fieldDesc.getEnumType().getFullName();
            enumType = toEnumModel(fieldDesc.getEnumType());
        }

        return FieldDescriptorModel.builder()
                .name(name)
                .jsonName(jsonName)
                .number(number)
                .type(type)
                .typeName(typeName)
                .repeated(isRepeated)
                .map(isMap)
                .optional(isOptional)
                .required(isRequired)
                .oneofIndex(oneofIndex)
                .oneofName(oneofName)
                .mapKeyType(mapKeyType)
                .mapValueType(mapValueType)
                .mapValueTypeName(mapValueTypeName)
                .nestedType(nestedType)
                .enumType(enumType)
                .build();
    }

    public static EnumDescriptorModel toEnumModel(Descriptors.EnumDescriptor enumDesc) {
        String name = enumDesc.getName();
        String fullName = enumDesc.getFullName();
        List<EnumValueModel> values = new ArrayList<>();
        for (Descriptors.EnumValueDescriptor valDesc : enumDesc.getValues()) {
            values.add(new EnumValueModel(valDesc.getName(), valDesc.getNumber(), null));
        }
        return new EnumDescriptorModel(name, fullName, values, null);
    }

    public static FieldValueType mapFieldType(Descriptors.FieldDescriptor.Type type) {
        return switch (type) {
            case DOUBLE -> FieldValueType.DOUBLE;
            case FLOAT -> FieldValueType.FLOAT;
            case INT64 -> FieldValueType.INT64;
            case UINT64 -> FieldValueType.UINT64;
            case INT32 -> FieldValueType.INT32;
            case FIXED64 -> FieldValueType.FIXED64;
            case FIXED32 -> FieldValueType.FIXED32;
            case BOOL -> FieldValueType.BOOL;
            case STRING -> FieldValueType.STRING;
            case GROUP -> FieldValueType.GROUP;
            case MESSAGE -> FieldValueType.MESSAGE;
            case BYTES -> FieldValueType.BYTES;
            case UINT32 -> FieldValueType.UINT32;
            case ENUM -> FieldValueType.ENUM;
            case SFIXED32 -> FieldValueType.SFIXED32;
            case SFIXED64 -> FieldValueType.SFIXED64;
            case SINT32 -> FieldValueType.SINT32;
            case SINT64 -> FieldValueType.SINT64;
        };
    }

    private static String getServiceComment(Descriptors.ServiceDescriptor serviceDesc) {
        // Can be extracted from FileDescriptorProto SourceCodeInfo when available
        return null;
    }
}
