package io.protonaut.core.models;

public enum FailureStrategy {
    STOP_ON_FAILURE,
    CONTINUE_ON_FAILURE,
    RETRY,
    SKIP,
    ABORT
}
