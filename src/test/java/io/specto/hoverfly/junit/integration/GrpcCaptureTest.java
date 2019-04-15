package io.specto.hoverfly.junit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.spanner.admin.instance.v1.InstanceAdminClient;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyMode;
import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import io.specto.hoverfly.junit.core.model.Simulation;
import io.specto.hoverfly.junit.grpc.GrpcConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static io.specto.hoverfly.junit.core.HoverflyConfig.remoteConfigs;
import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;

public class GrpcCaptureTest {


    private static Hoverfly hoverfly = new Hoverfly(new GrpcConfig().captureAllHeaders(), HoverflyMode.CAPTURE);
//    private static Hoverfly hoverfly = new Hoverfly(remoteConfigs().host("127.0.0.1").proxyPort(8500).adminPort(8888).captureAllHeaders(), HoverflyMode.CAPTURE);


    @BeforeClass
    public static void setUp() {
        hoverfly.start();
    }

    private PubSubClient pubSubClient = new PubSubClient();
    private SpannerClient spannerClient = new SpannerClient();

    @Test
    public void testListSubscriptions() throws Exception {

        SubscriptionAdminClient.ListSubscriptionsPagedResponse response = pubSubClient.listSubscriptions();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(0);
    }

    @Test
    public void testListTopics() throws Exception {

        TopicAdminClient.ListTopicsPagedResponse response1 = pubSubClient.listTopics();
        TopicAdminClient.ListTopicsPagedResponse response2 = pubSubClient.listTopics();

        assertThat(response1.getPage().getPageElementCount()).isEqualTo(1);
        assertThat(response2.getPage().getPageElementCount()).isEqualTo(1);
    }

    @Test
    public void testListInstances() throws IOException {
        InstanceAdminClient.ListInstancesPagedResponse response = spannerClient.listInstances();

        assertThat(response.getPage().getPageElementCount()).isEqualTo(0);
    }

    @Test
    public void testListTimeSeries() throws Exception {

        ByteArrayOutputStream got = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(got);
        System.setOut(out);

        StackDriverClient stackDriverClient = StackDriverClient.newInstance();

        stackDriverClient.listTimeSeries("metric.type=\"compute.googleapis.com/instance/cpu/utilization\"");
        stackDriverClient.listTimeSeries("metric.type=\"compute.googleapis.com/instance/cpu/utilization\"");

        assertThat(got.toString()).contains("Got timeseries:");
        assertThat(got.toString()).doesNotContain("Connection reset by peer");
    }

    @AfterClass
    public static void after() throws Exception {


        // Verify captured data is expected
        Path capturedFile = Paths.get("src/test/resources/hoverfly/captured.json");
        hoverfly.exportSimulation(capturedFile);
        hoverfly.close();
        final String capturedData = new String(Files.readAllBytes(capturedFile), defaultCharset());

        // Verify headers are captured
        ObjectMapper objectMapper = new ObjectMapper();
        Simulation simulation = objectMapper.readValue(capturedData, Simulation.class);
        Set<RequestResponsePair> pairs = simulation.getHoverflyData().getPairs();

        assertThat(pairs).hasSize(6);

        pairs.forEach(pair -> {
            assertThat(pair.getRequest().getHeaders()).isNotEmpty();
            assertThat(pair.getRequest().getMethod().get(0).getValue()).isEqualTo("POST");
            assertThat(pair.getRequest().getDestination().get(0).getValue()).asString().contains(".googleapis.com:443");
            assertThat(pair.getRequest().getHeaders().get("Content-Type").get(0).getValue()).isEqualTo("application/grpc");
            assertThat(pair.getResponse().getStatus()).isEqualTo(200);
            assertThat(pair.getResponse().getHeaders().get("Grpc-Status")).containsOnly("0");
            assertThat(pair.getResponse().getHeaders().get("Trailer")).contains("Grpc-Status");
        });

    }
}
