package io.specto.hoverfly.junit.grpc;

import io.specto.hoverfly.junit.core.config.LocalHoverflyConfig;

public class GrpcConfig extends LocalHoverflyConfig {

    private boolean enableGrpc;

    public GrpcConfig enableGrpc() {
        this.enableGrpc = true;
        return this;
    }

}
