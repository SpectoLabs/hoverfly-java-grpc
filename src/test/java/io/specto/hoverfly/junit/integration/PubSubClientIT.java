package io.specto.hoverfly.junit.integration;

import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PubSubClientIT {

//    @ClassRule
//    public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureMode("test.json",
//          HoverflyConfig.remoteConfigs().host("127.0.0.1").proxyPort(8500).adminPort(8888).proxyLocalHost());

    private PubSubClient pubSubClient;

    @Before
    public void setUp() {
        System.setProperty("projectId", StackDriverClientIT.getProjectId());
        pubSubClient = new PubSubClient();
    }

    @Test
    public void testListSubscriptions() throws Exception {

        SubscriptionAdminClient.ListSubscriptionsPagedResponse response = pubSubClient.listSubscriptions();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(0);
    }

    @Test
    public void testListTopics() throws Exception {
        // Act
        TopicAdminClient.ListTopicsPagedResponse response = pubSubClient.listTopics();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(1);
    }
}
