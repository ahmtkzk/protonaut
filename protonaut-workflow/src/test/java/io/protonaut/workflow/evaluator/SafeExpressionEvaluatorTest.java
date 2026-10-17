package io.protonaut.workflow.evaluator;

import io.protonaut.core.models.AssertionRule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SafeExpressionEvaluatorTest {

    private final String sampleJson = """
            {
              "name": "Selam",
              "age": 28,
              "active": true,
              "items": ["apple", "banana"],
              "profile": {
                "email": "selam@example.com"
              }
            }
            """;

    @Test
    void testEqualsOperator() {
        AssertionRule rule = new AssertionRule("response.name", "==", "Selam", null);
        var result = SafeExpressionEvaluator.evaluateAssertion(sampleJson, rule);
        assertThat(result.passed()).isTrue();

        AssertionRule failedRule = new AssertionRule("response.name", "==", "Other", null);
        var failedResult = SafeExpressionEvaluator.evaluateAssertion(sampleJson, failedRule);
        assertThat(failedResult.passed()).isFalse();
    }

    @Test
    void testNumericComparisons() {
        assertThat(SafeExpressionEvaluator.evaluateAssertion(sampleJson, new AssertionRule("response.age", ">=", "18", null)).passed()).isTrue();
        assertThat(SafeExpressionEvaluator.evaluateAssertion(sampleJson, new AssertionRule("response.age", ">", "30", null)).passed()).isFalse();
    }

    @Test
    void testArrayLength() {
        assertThat(SafeExpressionEvaluator.evaluateAssertion(sampleJson, new AssertionRule("response.items.length", ">", "0", null)).passed()).isTrue();
        assertThat(SafeExpressionEvaluator.evaluateAssertion(sampleJson, new AssertionRule("response.items.length", "==", "2", null)).passed()).isTrue();
    }

    @Test
    void testNestedPropertyAndContains() {
        assertThat(SafeExpressionEvaluator.evaluateAssertion(sampleJson, new AssertionRule("response.profile.email", "contains", "@example.com", null)).passed()).isTrue();
    }

    @Test
    void testConditionStringEvaluation() {
        assertThat(SafeExpressionEvaluator.evaluateCondition("response.name == \"Selam\"", sampleJson)).isTrue();
        assertThat(SafeExpressionEvaluator.evaluateCondition("response.age >= 21", sampleJson)).isTrue();
        assertThat(SafeExpressionEvaluator.evaluateCondition("response.items.length > 5", sampleJson)).isFalse();
    }
}
