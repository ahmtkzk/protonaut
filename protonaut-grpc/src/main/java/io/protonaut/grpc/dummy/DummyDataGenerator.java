package io.protonaut.grpc.dummy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.protonaut.core.models.*;

import java.util.*;

public class DummyDataGenerator {
    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final int MAX_DEPTH = 3;

    public static String generateJson(MessageDescriptorModel message) {
        if (message == null) return "{}";
        Map<String, Object> map = generateMap(message, 0);
        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private static Map<String, Object> generateMap(MessageDescriptorModel message, int depth) {
        if (depth > MAX_DEPTH || message == null) {
            return Map.of();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        Set<String> chosenOneofs = new HashSet<>();

        for (FieldDescriptorModel field : message.fields()) {
            // If field belongs to a oneof group, pick only the first field in that group
            if (field.oneofName() != null) {
                if (chosenOneofs.contains(field.oneofName())) {
                    continue;
                }
                chosenOneofs.add(field.oneofName());
            }

            String jsonKey = field.jsonName() != null ? field.jsonName() : field.name();
            Object value = generateFieldValue(field, depth);
            if (value != null) {
                result.put(jsonKey, value);
            }
        }

        return result;
    }

    private static Object generateFieldValue(FieldDescriptorModel field, int depth) {
        if (field.map()) {
            Map<String, Object> map = new LinkedHashMap<>();
            Object sampleKey = "key1";
            Object sampleValue = field.nestedType() != null
                    ? generateMap(field.nestedType(), depth + 1)
                    : generatePrimitiveValue(field.mapValueType(), field.name() + "_val");
            map.put((String) sampleKey, sampleValue);
            return map;
        }

        if (field.repeated()) {
            List<Object> list = new ArrayList<>();
            Object item = generateSingleValue(field, depth);
            if (item != null) {
                list.add(item);
            }
            return list;
        }

        return generateSingleValue(field, depth);
    }

    private static Object generateSingleValue(FieldDescriptorModel field, int depth) {
        if (field.type() == FieldValueType.MESSAGE) {
            // Well-known types
            if ("google.protobuf.Timestamp".equals(field.typeName())) {
                return "2026-10-16T20:00:00Z";
            }
            if ("google.protobuf.Duration".equals(field.typeName())) {
                return "60s";
            }
            if ("google.protobuf.StringValue".equals(field.typeName())) {
                return "sample text";
            }
            if ("google.protobuf.Int32Value".equals(field.typeName()) || "google.protobuf.Int64Value".equals(field.typeName())) {
                return 42;
            }
            if ("google.protobuf.BoolValue".equals(field.typeName())) {
                return true;
            }
            if (field.nestedType() != null) {
                return generateMap(field.nestedType(), depth + 1);
            }
            return Map.of();
        }

        if (field.type() == FieldValueType.ENUM) {
            if (field.enumType() != null && !field.enumType().values().isEmpty()) {
                // If there is more than 1 value, prefer the non-zero value for realistic testing
                List<EnumValueModel> vals = field.enumType().values();
                if (vals.size() > 1 && vals.get(0).number() == 0) {
                    return vals.get(1).name();
                }
                return vals.get(0).name();
            }
            return "UNSPECIFIED";
        }

        return generatePrimitiveValue(field.type(), field.name());
    }

    private static Object generatePrimitiveValue(FieldValueType type, String fieldName) {
        if (type == null) return "value";

        return switch (type) {
            case STRING -> generateRealisticString(fieldName);
            case INT32, SINT32, SFIXED32, UINT32, FIXED32 -> generateRealisticInt(fieldName);
            case INT64, SINT64, SFIXED64, UINT64, FIXED64 -> generateRealisticLong(fieldName);
            case DOUBLE, FLOAT -> 29.99;
            case BOOL -> true;
            case BYTES -> "SGVsbG8gUHJvdG9uYXV0"; // Base64 for "Hello Protonaut"
            default -> "test";
        };
    }

    private static String generateRealisticString(String fieldName) {
        if (fieldName == null) return "SAMPLE_VALUE";
        String lower = fieldName.toLowerCase().replace("_", "");

        if (lower.contains("firstname")) return "Selam";
        if (lower.contains("lastname")) return "Kazak";
        if (lower.contains("email")) return "selam@example.com";
        if (lower.contains("phone") || lower.contains("mobile")) return "+1-555-0199";
        if (lower.contains("city")) return "San Francisco";
        if (lower.contains("country")) return "USA";
        if (lower.contains("address")) return "100 Market St";
        if (lower.contains("zip") || lower.contains("postal")) return "94105";
        if (lower.contains("url") || lower.contains("link")) return "https://protonaut.dev";
        if (lower.contains("title")) return "Senior Software Engineer";
        if (lower.contains("description")) return "Demonstration request generated by Protonaut";
        if (lower.contains("token")) return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        if (lower.contains("uuid") || lower.contains("correlation")) return UUID.randomUUID().toString();
        if (lower.endsWith("id") || lower.equals("id")) return lower + "-1001";
        if (lower.contains("name")) return "Protonaut User";
        if (lower.contains("code")) return "PROTO-2026";
        if (lower.contains("status")) return "ACTIVE";

        return "TEST_" + fieldName.toUpperCase();
    }

    private static int generateRealisticInt(String fieldName) {
        if (fieldName == null) return 1;
        String lower = fieldName.toLowerCase().replace("_", "");
        if (lower.contains("age")) return 28;
        if (lower.contains("port")) return 6570;
        if (lower.contains("count") || lower.contains("quantity") || lower.contains("qty")) return 5;
        if (lower.contains("page")) return 1;
        if (lower.contains("limit") || lower.contains("size")) return 20;
        if (lower.contains("year")) return 2026;
        if (lower.endsWith("id")) return 101;
        return 42;
    }

    private static long generateRealisticLong(String fieldName) {
        if (fieldName == null) return 1000L;
        String lower = fieldName.toLowerCase().replace("_", "");
        if (lower.contains("timestamp") || lower.contains("time") || lower.contains("created") || lower.contains("updated")) {
            return System.currentTimeMillis();
        }
        if (lower.endsWith("id")) return 1001L;
        return 500L;
    }
}
