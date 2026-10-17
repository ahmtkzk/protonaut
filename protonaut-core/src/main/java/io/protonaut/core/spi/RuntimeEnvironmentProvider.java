package io.protonaut.core.spi;

import io.protonaut.core.models.EnvironmentInfo;

public interface RuntimeEnvironmentProvider {
    EnvironmentInfo getEnvironmentInfo();
}
