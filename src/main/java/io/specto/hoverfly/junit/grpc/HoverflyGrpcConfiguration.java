package io.specto.hoverfly.junit.grpc;

import io.specto.hoverfly.junit.core.SimulationPreprocessor;
import io.specto.hoverfly.junit.core.config.HoverflyConfiguration;
import io.specto.hoverfly.junit.core.config.LogLevel;
import org.slf4j.Logger;

import java.util.List;

class HoverflyGrpcConfiguration extends HoverflyConfiguration {

    HoverflyGrpcConfiguration(int proxyPort, int adminPort, boolean proxyLocalHost, String destination, String proxyCaCertificate,
                              List<String> captureHeaders, boolean webServer, Logger hoverflyLogger, LogLevel logLevel,
                              boolean statefulCapture, boolean incrementalCapture, SimulationPreprocessor preprocessor) {
        super(proxyPort, adminPort, proxyLocalHost, destination, proxyCaCertificate, captureHeaders, webServer,
                hoverflyLogger, logLevel, statefulCapture, incrementalCapture, preprocessor);
    }
}
