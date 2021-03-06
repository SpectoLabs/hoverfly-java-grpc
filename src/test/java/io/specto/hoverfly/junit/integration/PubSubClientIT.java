package io.specto.hoverfly.junit.integration;

import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import io.specto.hoverfly.junit.grpc.GrpcConfig;
import io.specto.hoverfly.junit.grpc.preprocessor.GcpApiSimulationPreprocessor;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.ClassRule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PubSubClientIT {

    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureOrSimulationMode("pubsub-api.json",
            new GrpcConfig().simulationPreprocessor(new GcpApiSimulationPreprocessor()));

    private PubSubClient pubSubClient = new PubSubClient();

    @Test
    public void testListSubscriptions() throws Exception {

        SubscriptionAdminClient.ListSubscriptionsPagedResponse response = pubSubClient.listSubscriptions();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(0);
    }

    @Test
    public void testListSubscriptionsMultipleTimes() throws Exception {

        SubscriptionAdminClient.ListSubscriptionsPagedResponse response1 = pubSubClient.listSubscriptions();
        SubscriptionAdminClient.ListSubscriptionsPagedResponse response2 = pubSubClient.listSubscriptions();

        assertThat(response1.getPage().getPageElementCount()).isEqualTo(0);
        assertThat(response2.getPage().getPageElementCount()).isEqualTo(0);
    }

    @Test
    public void testListTopics() throws Exception {

        TopicAdminClient.ListTopicsPagedResponse response = pubSubClient.listTopics();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(1);
    }
}
