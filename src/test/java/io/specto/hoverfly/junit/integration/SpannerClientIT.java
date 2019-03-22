package io.specto.hoverfly.junit.integration;

import com.google.cloud.spanner.admin.database.v1.DatabaseAdminClient;
import com.google.cloud.spanner.admin.instance.v1.InstanceAdminClient;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class SpannerClientIT {


    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureOrSimulationMode("spanner-api.json",
          HoverflyConfig.remoteConfigs().host("127.0.0.1").proxyPort(8500).adminPort(8888)
                  .simulationPreprocessor(new GcpApiSimulationPreprocessor()));

    private SpannerClient spannerClient;

    @Before
    public void setUp() {
        System.setProperty("projectId", StackDriverClientIT.getProjectId());
        spannerClient = new SpannerClient();
    }

    @Test
    public void testListInstances() throws IOException {
        InstanceAdminClient.ListInstancesPagedResponse response = spannerClient.listInstances();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(0);
    }

    @Test
    public void testListDatabases() throws IOException {
        DatabaseAdminClient.ListDatabasesPagedResponse response = spannerClient.listDatabases();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(0);
    }
}