package io.specto.hoverfly.junit.grpc;

import org.junit.Test;

import static io.specto.hoverfly.junit.grpc.HoverflyConfigValidator.findLicenseFileOnClasspath;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class HoverflyConfigValidatorTest {

    @Test
    public void shouldThrowExceptionIfLicenseFileIsNotFound() {

        assertThatThrownBy(() -> findLicenseFileOnClasspath("invalid"))
                .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("'hoverfly_license' files is not found in classpath");
    }
}