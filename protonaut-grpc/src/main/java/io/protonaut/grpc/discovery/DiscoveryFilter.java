package io.protonaut.grpc.discovery;

import java.util.List;
import java.util.regex.Pattern;

public class DiscoveryFilter {
    private final List<String> includeServices;
    private final List<String> excludeServices;
    private final List<String> includePackages;
    private final List<String> excludePackages;
    private final boolean reflectionExposed;

    public DiscoveryFilter(
            List<String> includeServices,
            List<String> excludeServices,
            List<String> includePackages,
            List<String> excludePackages,
            boolean reflectionExposed
    ) {
        this.includeServices = includeServices != null ? includeServices : List.of();
        this.excludeServices = excludeServices != null ? excludeServices : List.of();
        this.includePackages = includePackages != null ? includePackages : List.of();
        this.excludePackages = excludePackages != null ? excludePackages : List.of();
        this.reflectionExposed = reflectionExposed;
    }

    public static DiscoveryFilter defaultFilter() {
        return new DiscoveryFilter(List.of(), List.of(), List.of(), List.of(), false);
    }

    public boolean isServiceAllowed(String serviceFullName, String packageName, String serviceSimpleName) {
        if (serviceFullName == null) return false;

        // By default filter out gRPC internal reflection and health checks unless explicitly enabled
        if (!reflectionExposed) {
            if (serviceFullName.equals("grpc.reflection.v1alpha.ServerReflection") ||
                serviceFullName.equals("grpc.reflection.v1.ServerReflection") ||
                serviceFullName.equals("grpc.health.v1.Health")) {
                return false;
            }
        }

        // Package filters
        if (!includePackages.isEmpty()) {
            boolean pkgIncluded = includePackages.stream()
                    .anyMatch(p -> matches(packageName, p));
            if (!pkgIncluded) return false;
        }

        if (!excludePackages.isEmpty()) {
            boolean pkgExcluded = excludePackages.stream()
                    .anyMatch(p -> matches(packageName, p));
            if (pkgExcluded) return false;
        }

        // Service filters
        if (!includeServices.isEmpty()) {
            boolean svcIncluded = includeServices.stream()
                    .anyMatch(s -> matches(serviceFullName, s) || matches(serviceSimpleName, s));
            if (!svcIncluded) return false;
        }

        if (!excludeServices.isEmpty()) {
            boolean svcExcluded = excludeServices.stream()
                    .anyMatch(s -> matches(serviceFullName, s) || matches(serviceSimpleName, s));
            if (svcExcluded) return false;
        }

        return true;
    }

    private boolean matches(String value, String patternStr) {
        if (value == null || patternStr == null) return false;
        if (patternStr.contains("*") || patternStr.contains("?")) {
            String regex = patternStr
                    .replace(".", "\\.")
                    .replace("*", ".*")
                    .replace("?", ".");
            return Pattern.compile(regex).matcher(value).matches();
        }
        return value.equalsIgnoreCase(patternStr) || value.startsWith(patternStr + ".");
    }
}
