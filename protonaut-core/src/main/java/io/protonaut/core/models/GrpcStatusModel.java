package io.protonaut.core.models;

public record GrpcStatusModel(
        int code,
        String name,
        String description,
        String cause
) {
    public static final GrpcStatusModel OK = new GrpcStatusModel(0, "OK", "Success", null);

    public boolean isOk() {
        return code == 0;
    }
}
