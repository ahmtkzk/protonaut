package io.protonaut.workflow.interpolation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.protonaut.core.models.WorkflowStepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableInterpolator {
    private static final Logger log = LoggerFactory.getLogger(VariableInterpolator.class);
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String interpolate(
            String template,
            Map<String, WorkflowStepResult> previousSteps,
            Map<String, String> workflowVariables
    ) {
        if (template == null || !template.contains("{{")) {
            return template;
        }

        Matcher matcher = TOKEN_PATTERN.matcher(template);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String token = matcher.group(1).trim();
            String replacement = resolveToken(token, previousSteps, workflowVariables);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private static String resolveToken(
            String token,
            Map<String, WorkflowStepResult> previousSteps,
            Map<String, String> workflowVariables
    ) {
        // 1. Check workflow variables: {{vars.myVar}} or {{myVar}}
        if (workflowVariables != null) {
            if (token.startsWith("vars.")) {
                String varName = token.substring("vars.".length());
                String val = workflowVariables.get(varName);
                if (val != null) return val;
            } else if (workflowVariables.containsKey(token)) {
                return workflowVariables.get(token);
            }
        }

        // 2. Check environment variables: {{env.MY_ENV}}
        if (token.startsWith("env.")) {
            String envName = token.substring("env.".length());
            String envVal = System.getenv(envName);
            if (envVal != null) return envVal;
        }

        // 3. Check step response references: {{steps.getUser.response.userId}}
        if (token.startsWith("steps.") && previousSteps != null) {
            String path = token.substring("steps.".length());
            int firstDot = path.indexOf('.');
            if (firstDot > 0) {
                String stepId = path.substring(0, firstDot);
                String subPath = path.substring(firstDot + 1);

                WorkflowStepResult stepResult = previousSteps.get(stepId);
                if (stepResult == null) {
                    // Try case-insensitive lookup
                    stepResult = previousSteps.values().stream()
                            .filter(s -> s.stepId().equalsIgnoreCase(stepId) || s.stepName().equalsIgnoreCase(stepId))
                            .findFirst()
                            .orElse(null);
                }

                if (stepResult != null) {
                    if (subPath.equals("status")) {
                        return stepResult.status().name();
                    }
                    if (subPath.equals("durationMs")) {
                        return String.valueOf(stepResult.durationMs());
                    }
                    if (subPath.startsWith("response.") && stepResult.responseJson() != null) {
                        String jsonPath = subPath.substring("response.".length());
                        return extractFromJson(stepResult.responseJson(), jsonPath);
                    }
                }
            }
        }

        log.debug("Unresolved variable token: {{ {} }}", token);
        return "{{" + token + "}}";
    }

    private static String extractFromJson(String json, String path) {
        try {
            JsonNode current = MAPPER.readTree(json);
            String[] parts = path.split("\\.");
            for (String part : parts) {
                if (current != null && current.has(part)) {
                    current = current.get(part);
                } else {
                    return "";
                }
            }
            if (current != null) {
                return current.isTextual() ? current.asText() : current.toString();
            }
        } catch (Exception e) {
            log.trace("Error extracting json path {}: {}", path, e.getMessage());
        }
        return "";
    }
}
