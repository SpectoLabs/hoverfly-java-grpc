package io.specto.hoverfly.junit.integration;

import com.google.cloud.spanner.admin.database.v1.DatabaseAdminClient;
import com.google.cloud.spanner.admin.instance.v1.InstanceAdminClient;
import io.specto.hoverfly.junit.grpc.GrpcConfig;
import io.specto.hoverfly.junit.grpc.preprocessor.GcpApiSimulationPreprocessor;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class SpannerClientIT {


    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureOrSimulationMode("spanner-api.json",
          new GrpcConfig().simulationPreprocessor(new GcpApiSimulationPreprocessor()));

    private SpannerClient spannerClient = new SpannerClient();

    @Test
    public void testListInstances() throws IOException {
        InstanceAdminClient.ListInstancesPagedResponse response = spannerClient.listInstances();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(1);
    }

    @Test
    public void testListInstancesMultipleTimes() throws IOException {
        InstanceAdminClient.ListInstancesPagedResponse response1 = spannerClient.listInstances();
        InstanceAdminClient.ListInstancesPagedResponse response2 = spannerClient.listInstances();

        assertThat(response1.getPage().getPageElementCount()).isEqualTo(1);
        assertThat(response2.getPage().getPageElementCount()).isEqualTo(1);
    }

    @Test
    public void testListDatabases() throws IOException {
        DatabaseAdminClient.ListDatabasesPagedResponse response = spannerClient.listDatabases();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(0);
    }
}
