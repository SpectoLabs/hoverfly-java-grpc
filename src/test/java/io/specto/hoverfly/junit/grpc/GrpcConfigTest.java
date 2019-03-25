package io.specto.hoverfly.junit.grpc;


import io.specto.hoverfly.junit.core.config.HoverflyConfiguration;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GrpcConfigTest {

    @Test
    public void shouldOverrideDefaultBinaryName() {

        HoverflyConfiguration config = new GrpcConfig().build();

        assertThat(config.getBinaryNameFormat()).isEqualTo("hoverfly2_%s_%s%s");

    }

    @Test
    public void shouldThrowExceptionIfTryingToSetWebServerMode() {

        assertThatThrownBy(() -> new GrpcConfig().asWebServer().build())
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("Webserver mode is not implemented for Hoverfly gRPC yet.");
    }
}