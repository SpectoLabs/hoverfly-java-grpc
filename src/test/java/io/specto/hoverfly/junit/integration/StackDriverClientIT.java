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

public class StackDriverClientIT {
  private ByteArrayOutputStream bout;

  @Before
  public void setUp() {
    bout = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bout);
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
    StackDriverClient stackDriverClient = StackDriverClient.newInstance();

    stackDriverClient.listMetricDescriptors();
    // Assert
    String got = bout.toString();
    assertThat(got).contains("metricDescriptors/bigquery.googleapis.com/query/count");
  }

  @Ignore
  @Test
  public void testListTimeSeries() throws Exception {
    // Act
    StackDriverClient stackDriverClient = StackDriverClient.newInstance();

    stackDriverClient.listTimeSeries("metric.type=\"compute.googleapis.com/instance/cpu/utilization\"");

    // Assert
    String got = bout.toString();
    assertThat(got).contains("Got timeseries:");
  }

  @Ignore
  @Test
  public void testListTimeSeriesHeader() throws Exception {
    // Act
    StackDriverClient stackDriverClient = StackDriverClient.newInstance();

    stackDriverClient.listTimeSeriesHeaders();

    // Assert
    String got = bout.toString();
    assertThat(got).contains("Got timeseries headers:");
  }

  @Ignore
  @Test
  public void testListTimeSeriesAggregate() throws Exception {
    // Act
    StackDriverClient stackDriverClient = StackDriverClient.newInstance();

    stackDriverClient.listTimeSeriesAggregrate();

//     Assert
    String got = bout.toString();
    assertThat(got).contains("Got timeseries:");
  }

  @Ignore
  @Test
  public void testListTimeSeriesReduce() throws Exception {
    // Act
    StackDriverClient stackDriverClient = StackDriverClient.newInstance();

    stackDriverClient.listTimeSeriesReduce();

    // Assert
    String got = bout.toString();
    assertThat(got).contains("Got timeseries:");
  }

  @Test
  public void testGetResource() throws Exception {
    // Act
    StackDriverClient stackDriverClient = StackDriverClient.newInstance();

    stackDriverClient.describeMonitoredResources("cloudsql_database");

    // Assert
    String got = bout.toString();
    assertThat(got).contains("\"A database hosted in Google Cloud SQL");
  }

  @Test
  public void testListResources() throws Exception {
    // Act
    StackDriverClient stackDriverClient = StackDriverClient.newInstance();

    stackDriverClient.listMonitoredResources();

    // Assert
    String got = bout.toString();
    assertThat(got).contains("gce_instance");
  }
}
