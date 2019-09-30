package io.specto.hoverfly.junit.grpc;

import io.specto.hoverfly.junit.core.config.HoverflyConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        if (hoverflyConfig.isWebServer()) {
            throw new UnsupportedOperationException("Webserver mode is not implemented for Hoverfly gRPC yet.");
        }

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

        // Check proxy CA cert exists
        if (hoverflyConfig.getProxyCaCertificate().isPresent()) {
            checkResourceOnClasspath(hoverflyConfig.getProxyCaCertificate().get());
        }

        return hoverflyConfig;
    }

    /**
     * Looks for a license file on the classpath with the given name
     */
    static String findLicenseFileOnClasspath(String filepath) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return Optional.ofNullable(classLoader.getResource(filepath))
                .map(url -> {
                    try {
                        return url.toURI();
                    } catch (URISyntaxException e) {
                        throw new IllegalStateException("Failed to get license file path", e);
                    }
                })
                .map(Paths::get)
                .map(Path::toString)
                .orElseThrow(() -> new IllegalStateException("'hoverfly_license' files is not found in classpath"));
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
