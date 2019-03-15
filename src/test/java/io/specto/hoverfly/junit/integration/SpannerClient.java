package io.specto.hoverfly.junit.integration;


import com.google.cloud.spanner.admin.database.v1.DatabaseAdminClient;
import com.google.cloud.spanner.admin.instance.v1.InstanceAdminClient;
import com.google.spanner.admin.database.v1.InstanceName;
import com.google.spanner.admin.instance.v1.ProjectName;

import java.io.IOException;

public class SpannerClient {

    InstanceAdminClient.ListInstancesPagedResponse listInstances() throws IOException {
        InstanceAdminClient instanceAdminClient = InstanceAdminClient.create();
        String projectId = System.getProperty("projectId");
        return instanceAdminClient.listInstances(ProjectName.of(projectId));
    }

    DatabaseAdminClient.ListDatabasesPagedResponse listDatabases() throws IOException {
        DatabaseAdminClient dbAdminClient = DatabaseAdminClient.create();
        String projectId = System.getProperty("projectId");
        return dbAdminClient.listDatabases(InstanceName.of(projectId, "newrelic"));
    }

}
