/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.specto.hoverfly.junit.integration;

import io.specto.hoverfly.junit.grpc.GrpcConfig;
import io.specto.hoverfly.junit.grpc.preprocessor.GcpApiSimulationPreprocessor;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static io.specto.hoverfly.junit.core.HoverflyConfig.remoteConfigs;
import static org.assertj.core.api.Assertions.assertThat;

@Ignore

public class StackDriverClientIT {
  private ByteArrayOutputStream bout;
  private PrintStream out;
  private static final String LEGACY_PROJECT_ENV_NAME = "GCLOUD_PROJECT";
  private static final String PROJECT_ENV_NAME = "GOOGLE_CLOUD_PROJECT";

  static String getProjectId() {
    String projectId = System.getProperty(PROJECT_ENV_NAME, System.getenv(PROJECT_ENV_NAME));
    if (projectId == null) {
      projectId = System.getProperty(LEGACY_PROJECT_ENV_NAME,
          System.getenv(LEGACY_PROJECT_ENV_NAME));
    }
    return "hoverfly-cloud-dev-162815";
  }

  @Before
  public void setUp() {
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);
  }

  @After
  public void tearDown() {
    System.setOut(null);
  }

  @ClassRule
  public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureOrSimulationMode("stackdriver-api.json",
          new GrpcConfig().simulationPreprocessor(new GcpApiSimulationPreprocessor()));
//          remoteConfigs().host("127.0.0.1").proxyPort(8500).adminPort(8888).simulationPreprocessor(new GcpApiSimulationPreprocessor()));

  @Test
  public void testListMetricsDescriptor() throws Exception {
    // Act
    System.setProperty("projectId", StackDriverClientIT.getProjectId());
    StackDriverClient stackDriverClient = new StackDriverClient();

    stackDriverClient.listMetricDescriptors();
    // Assert
    String got = bout.toString();
    assertThat(got).contains("metricDescriptors/bigquery.googleapis.com/query/count");
  }

  @Test
  public void testListTimeSeries() throws Exception {
    // Act
    System.setProperty("projectId", StackDriverClientIT.getProjectId());
    StackDriverClient stackDriverClient = new StackDriverClient();

    stackDriverClient.listTimeSeries("metric.type=\"compute.googleapis.com/instance/cpu/utilization\"");

    // Assert
    String got = bout.toString();
    assertThat(got).contains("Got timeseries:");
  }

  @Test
  public void testListTimeSeriesHeader() throws Exception {
    // Act
    System.setProperty("projectId", StackDriverClientIT.getProjectId());
    StackDriverClient stackDriverClient = new StackDriverClient();

    stackDriverClient.listTimeSeriesHeaders();

    // Assert
    String got = bout.toString();
    assertThat(got).contains("Got timeseries headers:");
  }

  @Test
  public void testListTimeSeriesAggregate() throws Exception {
    // Act
    System.setProperty("projectId", StackDriverClientIT.getProjectId());
    StackDriverClient stackDriverClient = new StackDriverClient();

    stackDriverClient.listTimeSeriesAggregrate();

//     Assert
    String got = bout.toString();
    assertThat(got).contains("Got timeseries:");
  }

  @Test
  public void testListTimeSeriesReduce() throws Exception {
    // Act
    System.setProperty("projectId", StackDriverClientIT.getProjectId());
    StackDriverClient stackDriverClient = new StackDriverClient();

    stackDriverClient.listTimeSeriesReduce();

    // Assert
    String got = bout.toString();
    assertThat(got).contains("Got timeseries:");
  }

  @Test
  public void testGetResource() throws Exception {
    // Act
    System.setProperty("projectId", StackDriverClientIT.getProjectId());
    StackDriverClient stackDriverClient = new StackDriverClient();

    stackDriverClient.describeMonitoredResources("cloudsql_database");

    // Assert
    String got = bout.toString();
    assertThat(got).contains("\"A database hosted in Google Cloud SQL");
  }

  @Test
  public void testListResources() throws Exception {
    // Act
    System.setProperty("projectId", StackDriverClientIT.getProjectId());
    StackDriverClient stackDriverClient = new StackDriverClient();

    stackDriverClient.listMonitoredResources();

    // Assert
    String got = bout.toString();
    assertThat(got).contains("gce_instance");
  }
}
