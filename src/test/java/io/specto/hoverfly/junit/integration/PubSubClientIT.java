package io.specto.hoverfly.junit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import io.specto.hoverfly.junit.core.model.Simulation;
import io.specto.hoverfly.junit.grpc.GrpcConfig;
import io.specto.hoverfly.junit.grpc.preprocessor.GcpApiSimulationPreprocessor;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;

public class PubSubClientIT {

    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureMode("pubsub-api.json",
            new GrpcConfig().simulationPreprocessor(new GcpApiSimulationPreprocessor()));

    private PubSubClient pubSubClient = new PubSubClient();

    @Test
    public void testListSubscriptions() throws Exception {

        SubscriptionAdminClient.ListSubscriptionsPagedResponse response = pubSubClient.listSubscriptions();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(0);
    }

    @Test
    public void testListTopics() throws Exception {

        TopicAdminClient.ListTopicsPagedResponse response = pubSubClient.listTopics();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(1);
    }

    // We have to assert after the rule has executed because that's when the classpath is written to the filesystem
    @AfterClass
    public static void after() throws Exception {

        // Verify captured data is expected
        Path capturedFile = Paths.get("src/test/resources/hoverfly/pubsub-api.json");
        final String capturedData = new String(Files.readAllBytes(capturedFile), defaultCharset());

        // Verify headers are captured
        ObjectMapper objectMapper = new ObjectMapper();
        Simulation simulation = objectMapper.readValue(capturedData, Simulation.class);
        Set<RequestResponsePair> pairs = simulation.getHoverflyData().getPairs();

        assertThat(pairs).hasSize(2);

    }
}
