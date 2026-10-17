package io.protonaut.grpc.metadata;

import io.grpc.Metadata;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MetadataConverter {

    public static Metadata toGrpcMetadata(Map<String, String> headers) {
        Metadata metadata = new Metadata();
        if (headers == null || headers.isEmpty()) {
            return metadata;
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || value == null) continue;

            key = key.trim();
            if (key.endsWith("-bin")) {
                // Binary metadata key
                Metadata.Key<byte[]> metaKey = Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER);
                try {
                    byte[] bytes = Base64.getDecoder().decode(value);
                    metadata.put(metaKey, bytes);
                } catch (IllegalArgumentException e) {
                    metadata.put(metaKey, value.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                // ASCII metadata key
                Metadata.Key<String> metaKey = Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER);
                metadata.put(metaKey, value);
            }
        }
        return metadata;
    }

    public static Map<String, String> toMap(Metadata metadata) {
        Map<String, String> map = new HashMap<>();
        if (metadata == null) return map;

        for (String key : metadata.keys()) {
            if (key.endsWith("-bin")) {
                Metadata.Key<byte[]> metaKey = Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER);
                byte[] bytes = metadata.get(metaKey);
                if (bytes != null) {
                    map.put(key, Base64.getEncoder().encodeToString(bytes));
                }
            } else {
                Metadata.Key<String> metaKey = Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER);
                String val = metadata.get(metaKey);
                if (val != null) {
                    map.put(key, val);
                }
            }
        }
        return map;
    }
}
