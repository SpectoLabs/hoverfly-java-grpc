package io.specto.hoverfly.junit.grpc;

import io.specto.hoverfly.junit.core.config.HoverflyConfiguration;
import io.specto.hoverfly.junit.core.config.LocalHoverflyConfig;

public class GrpcConfig extends LocalHoverflyConfig {

    static final String DEFAULT_BINARY_NAME_FORMAT = "hoverfly2_%s_%s%s";

    private boolean enableGrpc;

    public GrpcConfig enableGrpc() {
        this.enableGrpc = true;
        return this;
    }

    @Override
    public HoverflyConfiguration build() {
        return super.build();
    }
}
