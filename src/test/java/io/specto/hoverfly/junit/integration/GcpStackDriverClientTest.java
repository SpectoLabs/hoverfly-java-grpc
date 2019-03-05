package io.specto.hoverfly.junit.integration;

import com.example.monitoring.QuickstartSample;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

public class GcpStackDriverClientTest {

    private ByteArrayOutputStream bout;
    private PrintStream out;
    private static final String LEGACY_PROJECT_ENV_NAME = "GCLOUD_PROJECT";
    private static final String PROJECT_ENV_NAME = "GOOGLE_CLOUD_PROJECT";


    private static String getProjectId() {
        String projectId = System.getProperty(PROJECT_ENV_NAME, System.getenv(PROJECT_ENV_NAME));
        if (projectId == null) {
            projectId = System.getProperty(LEGACY_PROJECT_ENV_NAME,
                    System.getenv(LEGACY_PROJECT_ENV_NAME));
        }
        return "hoverfly-cloud-dev-162815";
    }

//    @ClassRule
//    public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureMode("test.json");

    @Before
    public void setUp() {
        bout = new ByteArrayOutputStream();
        out = new PrintStream(bout);
        System.setOut(out);
    }

    @After
    public void tearDown() {
        System.setOut(null);
    }

    @Test
    public void testQuickstart() throws Exception {
        // Act
        System.setProperty("projectId", getProjectId());
        QuickstartSample.main();

        // Assert
        String got = bout.toString();
        assertThat(got).contains("Done writing time series data.");
    }
}
