package io.specto.hoverfly.junit.integration;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.nio.file.Paths;

public class GcpClientHelper {

    static final String PROJECT_ID = "hoverfly-cloud-dev-162815";

    static CredentialsProvider defaultCredentialsProvider() {
        return () -> GoogleCredentials.fromStream(new FileInputStream(Paths.get("test-service-account.json").toFile()));
    }
}
