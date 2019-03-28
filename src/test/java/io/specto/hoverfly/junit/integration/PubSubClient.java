package io.specto.hoverfly.junit.integration;

import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.ProjectName;

import java.io.IOException;

import static io.specto.hoverfly.junit.integration.GcpClientHelper.*;

class PubSubClient {

    SubscriptionAdminClient.ListSubscriptionsPagedResponse listSubscriptions() throws IOException {
        SubscriptionAdminSettings settings = SubscriptionAdminSettings.newBuilder().setCredentialsProvider(defaultCredentialsProvider()).build();
        SubscriptionAdminClient client = SubscriptionAdminClient.create(settings);
        return client.listSubscriptions(ProjectName.of(PROJECT_ID));
    }

    TopicAdminClient.ListTopicsPagedResponse listTopics() throws IOException {
        TopicAdminSettings settings = TopicAdminSettings.newBuilder().setCredentialsProvider(defaultCredentialsProvider()).build();
        TopicAdminClient topicAdminClient = TopicAdminClient.create(settings);
        return topicAdminClient.listTopics(ProjectName.of(PROJECT_ID));
    }
}
