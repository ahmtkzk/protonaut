package io.protonaut.core.models;

public record AssertionRule(
        String target,
        String operator,
        String expectedValue,
        String description
) {
    public enum Operator {
        EQUALS("=="),
        NOT_EQUALS("!="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREATER_OR_EQUAL(">="),
        LESS_OR_EQUAL("<="),
        CONTAINS("contains"),
        EXISTS("exists"),
        NOT_EXISTS("not_exists"),
        EMPTY("empty"),
        NOT_EMPTY("not_empty");

        private final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public static Operator from(String input) {
            if (input == null) return EQUALS;
            String trimmed = input.trim().toLowerCase();
            return switch (trimmed) {
                case "==", "equals", "eq" -> EQUALS;
                case "!=", "not_equals", "neq" -> NOT_EQUALS;
                case ">", "greater_than", "gt" -> GREATER_THAN;
                case "<", "less_than", "lt" -> LESS_THAN;
                case ">=", "greater_or_equal", "gte" -> GREATER_OR_EQUAL;
                case "<=", "less_or_equal", "lte" -> LESS_OR_EQUAL;
                case "contains" -> CONTAINS;
                case "exists" -> EXISTS;
                case "not_exists", "notexists" -> NOT_EXISTS;
                case "empty" -> EMPTY;
                case "not_empty", "notempty" -> NOT_EMPTY;
                default -> EQUALS;
            };
        }
    }
}
