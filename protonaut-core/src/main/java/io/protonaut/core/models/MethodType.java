package io.protonaut.core.models;

/**
 * gRPC RPC method communication pattern.
 */
public enum MethodType {
    UNARY,
    SERVER_STREAMING,
    CLIENT_STREAMING,
    BIDI_STREAMING;

    public boolean isStreaming() {
        return this != UNARY;
    }

    public boolean isClientStreaming() {
        return this == CLIENT_STREAMING || this == BIDI_STREAMING;
    }

    public boolean isServerStreaming() {
        return this == SERVER_STREAMING || this == BIDI_STREAMING;
    }
}
