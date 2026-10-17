package io.protonaut.core.models;

public record ProtoSourceModel(
        String filename,
        String packageName,
        String content,
        boolean reconstructed,
        String syntax
) {
    public static ProtoSourceModel original(String filename, String packageName, String content, String syntax) {
        return new ProtoSourceModel(filename, packageName, content, false, syntax != null ? syntax : "proto3");
    }

    public static ProtoSourceModel reconstructed(String filename, String packageName, String content, String syntax) {
        return new ProtoSourceModel(filename, packageName, content, true, syntax != null ? syntax : "proto3");
    }
}
