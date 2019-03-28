package io.specto.hoverfly.junit.integration;


import com.google.cloud.spanner.admin.database.v1.DatabaseAdminClient;
import com.google.cloud.spanner.admin.database.v1.DatabaseAdminSettings;
import com.google.cloud.spanner.admin.instance.v1.InstanceAdminClient;
import com.google.cloud.spanner.admin.instance.v1.InstanceAdminSettings;
import com.google.spanner.admin.database.v1.InstanceName;
import com.google.spanner.admin.instance.v1.ProjectName;

import java.io.IOException;

import static io.specto.hoverfly.junit.integration.GcpClientHelper.PROJECT_ID;
import static io.specto.hoverfly.junit.integration.GcpClientHelper.defaultCredentialsProvider;

class SpannerClient {

    InstanceAdminClient.ListInstancesPagedResponse listInstances() throws IOException {
        InstanceAdminSettings settings = InstanceAdminSettings.newBuilder().setCredentialsProvider(defaultCredentialsProvider()).build();
        InstanceAdminClient instanceAdminClient = InstanceAdminClient.create(settings);
        return instanceAdminClient.listInstances(ProjectName.of(PROJECT_ID));
    }

    DatabaseAdminClient.ListDatabasesPagedResponse listDatabases() throws IOException {
        DatabaseAdminSettings settings = DatabaseAdminSettings.newBuilder().setCredentialsProvider(defaultCredentialsProvider()).build();
        DatabaseAdminClient dbAdminClient = DatabaseAdminClient.create(settings);
        return dbAdminClient.listDatabases(InstanceName.of(PROJECT_ID, "newrelic"));
    }

}
