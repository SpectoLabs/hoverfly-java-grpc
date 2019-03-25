package io.specto.hoverfly.junit.grpc;

import io.specto.hoverfly.junit.core.config.HoverflyConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Optional;

/**
 * Validate user-input {@link GrpcConfig} before it is used by {@link io.specto.hoverfly.junit.core.Hoverfly}
 */
class HoverflyConfigValidator {

    /**
     * Sanity checking hoverfly configs and assign port number if necessary
     */
    HoverflyConfiguration validate(HoverflyConfiguration hoverflyConfig) {

        if (hoverflyConfig == null) {
            throw new IllegalArgumentException("HoverflyConfig cannot be null.");
        }

        // Validate local config
        else {
            // Validate custom ca cert and key
            boolean isKeyBlank = StringUtils.isBlank(hoverflyConfig.getSslKeyPath());
            boolean isCertBlank = StringUtils.isBlank(hoverflyConfig.getSslCertificatePath());
            if (isKeyBlank && !isCertBlank || !isKeyBlank && isCertBlank) {
                throw new IllegalArgumentException("Both SSL key and certificate files are required to override the default Hoverfly SSL.");
            }
            // Validate proxy port
            if (hoverflyConfig.getProxyPort() == 0) {
                hoverflyConfig.setProxyPort(findUnusedPort());
            }

            // Validate admin port
            if (hoverflyConfig.getAdminPort() == 0) {
                hoverflyConfig.setAdminPort(findUnusedPort());
            }
        }

        // Check proxy CA cert exists
        if (hoverflyConfig.getProxyCaCertificate().isPresent()) {
            checkResourceOnClasspath(hoverflyConfig.getProxyCaCertificate().get());
        }

        return hoverflyConfig;
    }


    /**
     * Looks for an unused port on the current machine
     */
    private static int findUnusedPort() {
        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Cannot find available port", e);
        }
    }

    private void checkResourceOnClasspath(String resourceName) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Optional.ofNullable(classLoader.getResource(resourceName))
                .orElseThrow(() -> new IllegalArgumentException("Resource not found with name: " + resourceName));
    }
}
