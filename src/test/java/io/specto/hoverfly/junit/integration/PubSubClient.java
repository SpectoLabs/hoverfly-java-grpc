package io.specto.hoverfly.junit.integration;

import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.ProjectName;

import java.io.IOException;

public class PubSubClient {

    SubscriptionAdminClient.ListSubscriptionsPagedResponse listSubscriptions() throws IOException {
        SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create();
        String projectId = System.getProperty("projectId");
        return subscriptionAdminClient.listSubscriptions(ProjectName.of(projectId));
    }

    TopicAdminClient.ListTopicsPagedResponse listTopics() throws IOException {
        TopicAdminClient topicAdminClient = TopicAdminClient.create();
        String projectId = System.getProperty("projectId");
        return topicAdminClient.listTopics(ProjectName.of(projectId));
    }
}
