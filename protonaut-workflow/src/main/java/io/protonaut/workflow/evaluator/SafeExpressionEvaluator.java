package io.protonaut.workflow.evaluator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.protonaut.core.models.AssertionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeExpressionEvaluator {
    private static final Logger log = LoggerFactory.getLogger(SafeExpressionEvaluator.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public record EvaluationResult(boolean passed, String errorMessage) {
        public static EvaluationResult success() {
            return new EvaluationResult(true, null);
        }
        public static EvaluationResult failure(String error) {
            return new EvaluationResult(false, error);
        }
    }

    public static EvaluationResult evaluateAssertion(String responseJson, AssertionRule rule) {
        if (rule == null) {
            return EvaluationResult.success();
        }

        try {
            JsonNode root = (responseJson != null && !responseJson.isBlank())
                    ? MAPPER.readTree(responseJson)
                    : null;

            String target = rule.target();
            if (target != null && target.startsWith("response.")) {
                target = target.substring("response.".length());
            }

            JsonNode targetNode = resolveNode(root, target);
            AssertionRule.Operator op = AssertionRule.Operator.from(rule.operator());
            String expected = cleanQuotes(rule.expectedValue());

            boolean result = evaluateOperator(targetNode, op, expected);
            if (result) {
                return EvaluationResult.success();
            } else {
                String actual = targetNode != null ? targetNode.asText() : "null";
                return EvaluationResult.failure(String.format(
                        "Assertion failed: %s %s %s (actual: %s)",
                        rule.target(), rule.operator(), rule.expectedValue(), actual
                ));
            }
        } catch (Exception e) {
            log.warn("Expression evaluation error: {}", e.getMessage());
            return EvaluationResult.failure("Evaluation error for " + rule.target() + ": " + e.getMessage());
        }
    }

    public static boolean evaluateCondition(String conditionStr, String responseJson) {
        if (conditionStr == null || conditionStr.isBlank()) {
            return true;
        }

        // Example condition format: response.name == "Selam" or response.age >= 18
        String trimmed = conditionStr.trim();
        String[] operators = {"==", "!=", ">=", "<=", ">", "<", " contains ", " exists", " not_exists", " empty", " not_empty"};

        for (String opSymbol : operators) {
            int idx = trimmed.indexOf(opSymbol);
            if (idx > 0) {
                String target = trimmed.substring(0, idx).trim();
                String operator = opSymbol.trim();
                String expected = trimmed.substring(idx + opSymbol.length()).trim();
                AssertionRule rule = new AssertionRule(target, operator, expected, null);
                return evaluateAssertion(responseJson, rule).passed();
            }
        }

        // Default check if boolean property exists and is true
        try {
            JsonNode root = MAPPER.readTree(responseJson);
            String path = trimmed.startsWith("response.") ? trimmed.substring("response.".length()) : trimmed;
            JsonNode node = resolveNode(root, path);
            return node != null && node.asBoolean(false);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean evaluateOperator(JsonNode node, AssertionRule.Operator op, String expected) {
        return switch (op) {
            case EXISTS -> node != null && !node.isMissingNode() && !node.isNull();
            case NOT_EXISTS -> node == null || node.isMissingNode() || node.isNull();
            case EMPTY -> node == null || node.isNull() || (node.isArray() && node.isEmpty()) || (node.isTextual() && node.asText().isEmpty());
            case NOT_EMPTY -> node != null && !node.isNull() && ((node.isArray() && !node.isEmpty()) || (node.isTextual() && !node.asText().isEmpty()) || node.isNumber());
            case CONTAINS -> node != null && node.asText().contains(expected);
            case EQUALS -> compareValues(node, expected) == 0;
            case NOT_EQUALS -> compareValues(node, expected) != 0;
            case GREATER_THAN -> compareValues(node, expected) > 0;
            case LESS_THAN -> compareValues(node, expected) < 0;
            case GREATER_OR_EQUAL -> compareValues(node, expected) >= 0;
            case LESS_OR_EQUAL -> compareValues(node, expected) <= 0;
        };
    }

    private static int compareValues(JsonNode node, String expected) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return expected == null || "null".equalsIgnoreCase(expected) ? 0 : -1;
        }

        if (node.isNumber()) {
            try {
                double nodeVal = node.asDouble();
                double expectedVal = Double.parseDouble(expected);
                return Double.compare(nodeVal, expectedVal);
            } catch (NumberFormatException ignored) {}
        }

        if (node.isBoolean()) {
            boolean nodeVal = node.asBoolean();
            boolean expectedVal = Boolean.parseBoolean(expected);
            return Boolean.compare(nodeVal, expectedVal);
        }

        return node.asText().compareTo(expected);
    }

    private static JsonNode resolveNode(JsonNode root, String path) {
        if (root == null || path == null || path.isBlank()) return null;

        String[] parts = path.split("\\.");
        JsonNode current = root;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.equals("length") && i == parts.length - 1 && current.isArray()) {
                // Return length as integer node
                return MAPPER.getNodeFactory().numberNode(current.size());
            }

            if (current.has(part)) {
                current = current.get(part);
            } else {
                return null;
            }
        }

        return current;
    }

    private static String cleanQuotes(String val) {
        if (val == null) return "";
        String s = val.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}
