package io.specto.hoverfly.junit.grpc;

import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.config.HoverflyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcConfig extends HoverflyConfig {

    private static final String DEFAULT_BINARY_NAME_FORMAT = "hoverfly2_%s_%s%s";
    private Logger hoverflyLogger = LoggerFactory.getLogger("hoverfly-grpc");

    @Override
    public HoverflyConfiguration build() {
        HoverflyConfiguration configs = new HoverflyGrpcConfiguration(proxyPort, adminPort, proxyLocalHost, destination,
                proxyCaCert, captureHeaders, webServer, hoverflyLogger, statefulCapture);
        configs.setSimulationPreprocessor(this.simulationPreprocessor);
        configs.setBinaryNameFormat(DEFAULT_BINARY_NAME_FORMAT);
        HoverflyConfigValidator validator = new HoverflyConfigValidator();
        return validator.validate(configs);
    }
}
