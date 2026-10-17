package io.protonaut.grpc.protobuf;

import com.google.protobuf.Descriptors;
import io.protonaut.core.models.ProtoSourceModel;

import java.util.HashSet;
import java.util.Set;

public class ProtoReconstructor {

    public static ProtoSourceModel reconstructServiceProto(Descriptors.ServiceDescriptor serviceDesc) {
        Descriptors.FileDescriptor fileDesc = serviceDesc.getFile();
        StringBuilder sb = new StringBuilder();

        sb.append("// ================================================================\n");
        sb.append("// [RECONSTRUCTED PROTOBUF DEFINITION]\n");
        sb.append("// Original .proto source was not packaged with the binary.\n");
        sb.append("// Reconstructed by Protonaut from compiled runtime descriptors.\n");
        sb.append("// ================================================================\n\n");

        String syntax = fileDesc.toProto().hasSyntax() ? fileDesc.toProto().getSyntax() : "proto3";
        sb.append("syntax = \"").append(syntax).append("\";\n\n");

        if (!fileDesc.getPackage().isEmpty()) {
            sb.append("package ").append(fileDesc.getPackage()).append(";\n\n");
        }

        // Imports
        for (Descriptors.FileDescriptor dep : fileDesc.getDependencies()) {
            sb.append("import \"").append(dep.getName()).append("\";\n");
        }
        if (!fileDesc.getDependencies().isEmpty()) {
            sb.append("\n");
        }

        // Reconstruct Enums in file
        for (Descriptors.EnumDescriptor enumDesc : fileDesc.getEnumTypes()) {
            reconstructEnum(sb, enumDesc, "");
        }

        // Reconstruct Messages in file
        Set<String> renderedMessages = new HashSet<>();
        for (Descriptors.Descriptor msgDesc : fileDesc.getMessageTypes()) {
            reconstructMessage(sb, msgDesc, "", renderedMessages);
        }

        // Reconstruct Services
        for (Descriptors.ServiceDescriptor s : fileDesc.getServices()) {
            reconstructService(sb, s);
        }

        return ProtoSourceModel.reconstructed(
                fileDesc.getName(),
                fileDesc.getPackage(),
                sb.toString(),
                syntax
        );
    }

    private static void reconstructService(StringBuilder sb, Descriptors.ServiceDescriptor service) {
        sb.append("service ").append(service.getName()).append(" {\n");
        for (Descriptors.MethodDescriptor method : service.getMethods()) {
            sb.append("  rpc ").append(method.getName()).append(" (");
            if (method.isClientStreaming()) {
                sb.append("stream ");
            }
            sb.append(method.getInputType().getName()).append(") returns (");
            if (method.isServerStreaming()) {
                sb.append("stream ");
            }
            sb.append(method.getOutputType().getName()).append(");\n");
        }
        sb.append("}\n\n");
    }

    private static void reconstructMessage(StringBuilder sb, Descriptors.Descriptor message, String indent, Set<String> rendered) {
        if (!rendered.add(message.getFullName())) {
            return;
        }

        sb.append(indent).append("message ").append(message.getName()).append(" {\n");

        // Nested enums
        for (Descriptors.EnumDescriptor enumDesc : message.getEnumTypes()) {
            reconstructEnum(sb, enumDesc, indent + "  ");
        }

        // Nested messages (excluding map entries)
        for (Descriptors.Descriptor nested : message.getNestedTypes()) {
            if (!nested.getOptions().getMapEntry()) {
                reconstructMessage(sb, nested, indent + "  ", rendered);
            }
        }

        // Fields and Oneofs
        Set<Descriptors.FieldDescriptor> oneofFields = new HashSet<>();
        for (Descriptors.OneofDescriptor oneof : message.getOneofs()) {
            sb.append(indent).append("  oneof ").append(oneof.getName()).append(" {\n");
            for (Descriptors.FieldDescriptor field : oneof.getFields()) {
                oneofFields.add(field);
                renderField(sb, field, indent + "    ");
            }
            sb.append(indent).append("  }\n");
        }

        for (Descriptors.FieldDescriptor field : message.getFields()) {
            if (!oneofFields.contains(field)) {
                renderField(sb, field, indent + "  ");
            }
        }

        sb.append(indent).append("}\n\n");
    }

    private static void renderField(StringBuilder sb, Descriptors.FieldDescriptor field, String indent) {
        sb.append(indent);
        if (field.isMapField()) {
            Descriptors.Descriptor mapEntry = field.getMessageType();
            Descriptors.FieldDescriptor key = mapEntry.findFieldByNumber(1);
            Descriptors.FieldDescriptor val = mapEntry.findFieldByNumber(2);
            sb.append("map<").append(typeName(key)).append(", ").append(typeName(val)).append("> ");
        } else {
            if (field.isRepeated()) {
                sb.append("repeated ");
            } else if (field.isOptional()) {
                sb.append("optional ");
            }
            sb.append(typeName(field)).append(" ");
        }

        sb.append(field.getName()).append(" = ").append(field.getNumber()).append(";\n");
    }

    private static void reconstructEnum(StringBuilder sb, Descriptors.EnumDescriptor enumDesc, String indent) {
        sb.append(indent).append("enum ").append(enumDesc.getName()).append(" {\n");
        for (Descriptors.EnumValueDescriptor val : enumDesc.getValues()) {
            sb.append(indent).append("  ").append(val.getName()).append(" = ").append(val.getNumber()).append(";\n");
        }
        sb.append(indent).append("}\n\n");
    }

    private static String typeName(Descriptors.FieldDescriptor field) {
        return switch (field.getType()) {
            case DOUBLE -> "double";
            case FLOAT -> "float";
            case INT64 -> "int64";
            case UINT64 -> "uint64";
            case INT32 -> "int32";
            case FIXED64 -> "fixed64";
            case FIXED32 -> "fixed32";
            case BOOL -> "bool";
            case STRING -> "string";
            case GROUP -> field.getMessageType().getName();
            case MESSAGE -> field.getMessageType().getName();
            case BYTES -> "bytes";
            case UINT32 -> "uint32";
            case ENUM -> field.getEnumType().getName();
            case SFIXED32 -> "sfixed32";
            case SFIXED64 -> "sfixed64";
            case SINT32 -> "sint32";
            case SINT64 -> "sint64";
        };
    }
}
