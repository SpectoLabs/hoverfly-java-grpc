package io.specto.hoverfly.junit.grpc;

import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.config.HoverflyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static io.specto.hoverfly.junit.grpc.HoverflyConfigValidator.findLicenseFileOnClasspath;

/**
 * Config builder for enabling gRPC support in {@link io.specto.hoverfly.junit.core.Hoverfly}
 */
public class GrpcConfig extends HoverflyConfig {

    private static final String DEFAULT_LICENCE_FILE_NAME = "hoverfly_license";
    private static final String DEFAULT_BINARY_NAME_FORMAT = "hoverfly2_%s_%s%s";
    private Logger hoverflyLogger = LoggerFactory.getLogger("hoverfly-grpc");


    @Override
    public HoverflyConfiguration build() {
        HoverflyConfiguration configs = new HoverflyGrpcConfiguration(proxyPort, adminPort, proxyLocalHost, destination,
                proxyCaCert, captureHeaders, webServer, hoverflyLogger, statefulCapture, simulationPreprocessor);
        configs.setBinaryNameFormat(DEFAULT_BINARY_NAME_FORMAT);
        configs.setCommands(Arrays.asList("-license-path", findLicenseFileOnClasspath(DEFAULT_LICENCE_FILE_NAME)));
        HoverflyConfigValidator validator = new HoverflyConfigValidator();
        return validator.validate(configs);
    }
}
