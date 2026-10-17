package io.protonaut.core.metadata;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MetadataGenerator {
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    /**
     * Replaces placeholder tokens in metadata value:
     * - {{uuid}}
     * - {{timestamp}} / {{unixTimestamp}}
     * - {{isoTimestamp}}
     * - {{randomString}}
     * - {{sequence}}
     */
    public static String resolveValue(String template) {
        if (template == null || !template.contains("{{")) {
            return template;
        }

        String result = template;
        if (result.contains("{{uuid}}")) {
            result = result.replace("{{uuid}}", UUID.randomUUID().toString());
        }
        if (result.contains("{{timestamp}}")) {
            result = result.replace("{{timestamp}}", String.valueOf(System.currentTimeMillis()));
        }
        if (result.contains("{{unixTimestamp}}")) {
            result = result.replace("{{unixTimestamp}}", String.valueOf(Instant.now().getEpochSecond()));
        }
        if (result.contains("{{isoTimestamp}}")) {
            result = result.replace("{{isoTimestamp}}", Instant.now().toString());
        }
        if (result.contains("{{randomString}}")) {
            result = result.replace("{{randomString}}", UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        }
        if (result.contains("{{sequence}}")) {
            result = result.replace("{{sequence}}", String.valueOf(SEQUENCE.getAndIncrement()));
        }
        return result;
    }

    /**
     * Resolves all metadata template values in the map.
     */
    public static Map<String, String> resolveMetadata(Map<String, String> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return Map.of();
        }
        Map<String, String> resolved = new HashMap<>();
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (isValidKey(key)) {
                resolved.put(key, resolveValue(value));
            }
        }
        return resolved;
    }

    /**
     * Validate gRPC metadata ASCII key: letters, digits, underscore, hyphen, dot.
     */
    public static boolean isValidKey(String key) {
        if (key == null || key.isBlank()) return false;
        return key.matches("^[a-zA-Z0-9_.-]+$");
    }
}
