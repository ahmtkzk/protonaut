package io.protonaut.core.models;

public enum FieldValueType {
    DOUBLE,
    FLOAT,
    INT64,
    UINT64,
    INT32,
    FIXED64,
    FIXED32,
    BOOL,
    STRING,
    GROUP,
    MESSAGE,
    BYTES,
    UINT32,
    ENUM,
    SFIXED32,
    SFIXED64,
    SINT32,
    SINT64;

    public boolean isNumeric() {
        return switch (this) {
            case DOUBLE, FLOAT, INT64, UINT64, INT32, FIXED64, FIXED32, UINT32, SFIXED32, SFIXED64, SINT32, SINT64 -> true;
            default -> false;
        };
    }

    public boolean isString() {
        return this == STRING;
    }

    public boolean isBoolean() {
        return this == BOOL;
    }

    public boolean isMessage() {
        return this == MESSAGE;
    }

    public boolean isEnum() {
        return this == ENUM;
    }
}
