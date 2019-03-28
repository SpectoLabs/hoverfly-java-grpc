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

import com.google.api.Metric;
import com.google.api.MetricDescriptor;
import com.google.api.MonitoredResource;
import com.google.api.MonitoredResourceDescriptor;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.cloud.monitoring.v3.MetricServiceClient.ListMetricDescriptorsPagedResponse;
import com.google.cloud.monitoring.v3.MetricServiceClient.ListMonitoredResourceDescriptorsPagedResponse;
import com.google.cloud.monitoring.v3.MetricServiceClient.ListTimeSeriesPagedResponse;
import com.google.cloud.monitoring.v3.MetricServiceSettings;
import com.google.monitoring.v3.*;
import com.google.protobuf.Duration;
import com.google.protobuf.util.Timestamps;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.specto.hoverfly.junit.integration.GcpClientHelper.PROJECT_ID;
import static io.specto.hoverfly.junit.integration.GcpClientHelper.defaultCredentialsProvider;


public class StackDriverClient {

  private static final String CUSTOM_METRIC_DOMAIN = "custom.googleapis.com";
  private static final long CURRENT_TIME_MILLIS = ZonedDateTime.of(2019, 3, 28, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant().toEpochMilli();
  private static final ProjectName PROJECT_NAME = ProjectName.of(PROJECT_ID);

  private MetricServiceClient client;

  private StackDriverClient(MetricServiceClient client) {
    this.client = client;
  }

  public static StackDriverClient newInstance() throws IOException {
    MetricServiceSettings settings = MetricServiceSettings.newBuilder().setCredentialsProvider(defaultCredentialsProvider()).build();
    final MetricServiceClient client = MetricServiceClient.create(settings);
    return new StackDriverClient(client);
  }

  /**
   * Exercises the methods defined in this class.
   * <p>
   * <p>Assumes that you are authenticated using the Google Cloud SDK (using
   * {@code gcloud auth application-default-login}).
   */
  public static void main(String[] args) throws Exception {

    StackDriverClient stackDriverClient = newInstance();
    System.out.println("Stackdriver Monitoring snippets");
    System.out.println();
    printUsage();
    while (true) {
      String commandLine = System.console().readLine("> ");
      if (commandLine.trim().isEmpty()) {
        break;
      }
      try {
        stackDriverClient.handleCommandLine(commandLine);
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
        printUsage();
      }
    }
    System.out.println("exiting");
    System.exit(0);
  }

  /**
   * Creates a metric descriptor.
   * <p>
   * See: https://cloud.google.com/monitoring/api/ref_v3/rest/v3/projects.metricDescriptors/create
   *
   * @param type The metric type
   */
  void createMetricDescriptor(String type) {
    // [START monitoring_create_metric]
    String metricType = CUSTOM_METRIC_DOMAIN + "/" + type;

    MetricDescriptor descriptor = MetricDescriptor.newBuilder()
        .setType(metricType)
        .setDescription("This is a simple example of a custom metric.")
        .setMetricKind(MetricDescriptor.MetricKind.GAUGE)
        .setValueType(MetricDescriptor.ValueType.DOUBLE)
        .build();

    CreateMetricDescriptorRequest request = CreateMetricDescriptorRequest.newBuilder()
        .setName(PROJECT_NAME.toString())
        .setMetricDescriptor(descriptor)
        .build();

    client.createMetricDescriptor(request);
    // [END monitoring_create_metric]
  }

  /**
   * Delete a metric descriptor.
   *
   * @param name Name of metric descriptor to delete
   */
  void deleteMetricDescriptor(String name) {
    // [START monitoring_delete_metric]
    MetricDescriptorName metricName = MetricDescriptorName.of(PROJECT_ID, name);
    client.deleteMetricDescriptor(metricName);
    System.out.println("Deleted descriptor " + name);
    // [END monitoring_delete_metric]
  }

  /**
   * Demonstrates writing a time series value for the metric type
   * 'custom.google.apis.com/my_metric'.
   * <p>
   * This method assumes `my_metric` descriptor has already been created as a
   * DOUBLE value_type and GAUGE metric kind. If the metric descriptor
   * doesn't exist, it will be auto-created.
   */
  //CHECKSTYLE OFF: VariableDeclarationUsageDistance
  void writeTimeSeries() {
    // [START monitoring_write_timeseries]

    // Prepares an individual data point
    
    TimeInterval interval = TimeInterval.newBuilder()
        .setEndTime(Timestamps.fromMillis(CURRENT_TIME_MILLIS))
        .build();
    TypedValue value = TypedValue.newBuilder()
        .setDoubleValue(123.45)
        .build();
    Point point = Point.newBuilder()
        .setInterval(interval)
        .setValue(value)
        .build();

    List<Point> pointList = new ArrayList<>();
    pointList.add(point);

    // Prepares the metric descriptor
    Map<String, String> metricLabels = new HashMap<>();
    Metric metric = Metric.newBuilder()
        .setType("custom.googleapis.com/my_metric")
        .putAllLabels(metricLabels)
        .build();

    // Prepares the monitored resource descriptor
    Map<String, String> resourceLabels = new HashMap<>();
    resourceLabels.put("instance_id", "1234567890123456789");
    resourceLabels.put("zone", "us-central1-f");

    MonitoredResource resource = MonitoredResource.newBuilder()
        .setType("gce_instance")
        .putAllLabels(resourceLabels)
        .build();

    // Prepares the time series request
    TimeSeries timeSeries = TimeSeries.newBuilder()
        .setMetric(metric)
        .setResource(resource)
        .addAllPoints(pointList)
        .build();

    List<TimeSeries> timeSeriesList = new ArrayList<>();
    timeSeriesList.add(timeSeries);

    CreateTimeSeriesRequest request = CreateTimeSeriesRequest.newBuilder()
        .setName(PROJECT_NAME.toString())
        .addAllTimeSeries(timeSeriesList)
        .build();

    // Writes time series data
    client.createTimeSeries(request);
    System.out.println("Done writing time series value.");
    // [END monitoring_write_timeseries]
  }
  //CHECKSTYLE ON: VariableDeclarationUsageDistance

  /**
   * Demonstrates listing time series headers.
   */
  void listTimeSeriesHeaders() {
    // [START monitoring_read_timeseries_fields]

    // Restrict time to last 20 minutes
    long startMillis = CURRENT_TIME_MILLIS - ((60 * 20) * 1000);
    TimeInterval interval = TimeInterval.newBuilder()
        .setStartTime(Timestamps.fromMillis(startMillis))
        .setEndTime(Timestamps.fromMillis(CURRENT_TIME_MILLIS))
        .build();

    ListTimeSeriesRequest.Builder requestBuilder = ListTimeSeriesRequest.newBuilder()
        .setName(PROJECT_NAME.toString())
        .setFilter("metric.type=\"compute.googleapis.com/instance/cpu/utilization\"")
        .setInterval(interval)
        .setView(ListTimeSeriesRequest.TimeSeriesView.HEADERS);

    ListTimeSeriesRequest request = requestBuilder.build();

    ListTimeSeriesPagedResponse response = client.listTimeSeries(request);

    System.out.println("Got timeseries headers: ");
    for (TimeSeries ts : response.iterateAll()) {
      System.out.println(ts);
    }
    // [END monitoring_read_timeseries_fields]
  }

  /**
   * Demonstrates listing time series using a filter.
   */
  void listTimeSeries(String filter) {
    // [START monitoring_read_timeseries_simple]

    // Restrict time to last 20 minutes
    long startMillis = CURRENT_TIME_MILLIS - ((60 * 20) * 1000);
    TimeInterval interval = TimeInterval.newBuilder()
        .setStartTime(Timestamps.fromMillis(startMillis))
        .setEndTime(Timestamps.fromMillis(CURRENT_TIME_MILLIS))
        .build();

    ListTimeSeriesRequest.Builder requestBuilder = ListTimeSeriesRequest.newBuilder()
        .setName(PROJECT_NAME.toString())
        .setFilter(filter)
        .setInterval(interval);

    ListTimeSeriesRequest request = requestBuilder.build();

    ListTimeSeriesPagedResponse response = client.listTimeSeries(request);

    System.out.println("Got timeseries: ");
    for (TimeSeries ts : response.iterateAll()) {
      System.out.println(ts);
    }
    // [END monitoring_read_timeseries_simple]
  }

  /**
   * Demonstrates listing time series and aggregating them.
   */
  void listTimeSeriesAggregrate() {
    // [START monitoring_read_timeseries_align]

    // Restrict time to last 20 minutes
    long startMillis = CURRENT_TIME_MILLIS - ((60 * 20) * 1000);
    TimeInterval interval = TimeInterval.newBuilder()
        .setStartTime(Timestamps.fromMillis(startMillis))
        .setEndTime(Timestamps.fromMillis(CURRENT_TIME_MILLIS))
        .build();

    Aggregation aggregation = Aggregation.newBuilder()
        .setAlignmentPeriod(Duration.newBuilder().setSeconds(600).build())
        .setPerSeriesAligner(Aggregation.Aligner.ALIGN_MEAN)
        .build();

    ListTimeSeriesRequest.Builder requestBuilder = ListTimeSeriesRequest.newBuilder()
        .setName(PROJECT_NAME.toString())
        .setFilter("metric.type=\"compute.googleapis.com/instance/cpu/utilization\"")
        .setInterval(interval)
        .setAggregation(aggregation);

    ListTimeSeriesRequest request = requestBuilder.build();

    ListTimeSeriesPagedResponse response = client.listTimeSeries(request);

    System.out.println("Got timeseries: ");
    for (TimeSeries ts : response.iterateAll()) {
      System.out.println(ts);
    }
    // [END monitoring_read_timeseries_align]
  }

  /**
   * Demonstrates listing time series and aggregating and reducing them.
   */
  void listTimeSeriesReduce() throws IOException {
    // [START monitoring_read_timeseries_reduce]

    // Restrict time to last 20 minutes
    long startMillis = CURRENT_TIME_MILLIS - ((60 * 20) * 1000);
    TimeInterval interval = TimeInterval.newBuilder()
        .setStartTime(Timestamps.fromMillis(startMillis))
        .setEndTime(Timestamps.fromMillis(CURRENT_TIME_MILLIS))
        .build();

    Aggregation aggregation = Aggregation.newBuilder()
        .setAlignmentPeriod(Duration.newBuilder().setSeconds(600).build())
        .setPerSeriesAligner(Aggregation.Aligner.ALIGN_MEAN)
        .setCrossSeriesReducer(Aggregation.Reducer.REDUCE_MEAN)
        .build();

    ListTimeSeriesRequest.Builder requestBuilder = ListTimeSeriesRequest.newBuilder()
        .setName(PROJECT_NAME.toString())
        .setFilter("metric.type=\"compute.googleapis.com/instance/cpu/utilization\"")
        .setInterval(interval)
        .setAggregation(aggregation);

    ListTimeSeriesRequest request = requestBuilder.build();

    ListTimeSeriesPagedResponse response = client.listTimeSeries(request);

    System.out.println("Got timeseries: ");
    for (TimeSeries ts : response.iterateAll()) {
      System.out.println(ts);
    }
    // [END monitoring_read_timeseries_reduce]
  }

  /**
   * Returns the first page of all metric descriptors.
   */
  void listMetricDescriptors() {
    // [START monitoring_list_descriptors]

    ListMetricDescriptorsRequest request = ListMetricDescriptorsRequest
        .newBuilder()
        .setName(PROJECT_NAME.toString())
        .build();
    ListMetricDescriptorsPagedResponse response = client.listMetricDescriptors(request);

    System.out.println("Listing descriptors: ");

    for (MetricDescriptor d : response.iterateAll()) {
      System.out.println(d.getName() + " " + d.getDisplayName());
    }
    // [END monitoring_list_descriptors]
  }

  /**
   * Gets all monitored resource descriptors.
   */
  void listMonitoredResources() {
    // [START monitoring_list_resources]

    ListMonitoredResourceDescriptorsRequest request = ListMonitoredResourceDescriptorsRequest
        .newBuilder()
        .setName(PROJECT_NAME.toString())
        .build();

    System.out.println("Listing monitored resource descriptors: ");

    ListMonitoredResourceDescriptorsPagedResponse response = client
        .listMonitoredResourceDescriptors(request);

    for (MonitoredResourceDescriptor d : response.iterateAll()) {
      System.out.println(d.getType());
    }
    // [END monitoring_list_resources]
  }

  /**
   * Gets full information for a monitored resource.
   *
   * @param type The resource type
   */
  void describeMonitoredResources(String type) {
    // [START monitoring_get_descriptor]

    MonitoredResourceDescriptorName name = MonitoredResourceDescriptorName.of(PROJECT_ID, type);
    MonitoredResourceDescriptor response = client.getMonitoredResourceDescriptor(name);

    System.out.println("Printing monitored resource descriptor: ");
    System.out.println(response);
    // [END monitoring_get_descriptor]
  }


  /**
   * Handles a single command.
   *
   * @param commandLine A line of input provided by the user
   */
  void handleCommandLine(String commandLine) throws IOException {
    String[] args = commandLine.split("\\s+");

    if (args.length < 1) {
      throw new IllegalArgumentException("not enough args");
    }

    String command = args[0];
    switch (command) {
      case "new-metric-descriptor":
        // Everything after the first whitespace token is interpreted to be the description.
        args = commandLine.split("\\s+", 2);
        if (args.length != 2) {
          throw new IllegalArgumentException("usage: <type>");
        }
        // Set created to now() and done to false.
        createMetricDescriptor(args[1]);
        System.out.println("Metric descriptor created");
        break;
      case "list-metric-descriptors":
        args = commandLine.split("\\s+", 2);
        if (args.length != 1) {
          throw new IllegalArgumentException("usage: no arguments");
        }
        listMetricDescriptors();
        break;
      case "list-monitored-resources":
        args = commandLine.split("\\s+", 2);
        if (args.length != 1) {
          throw new IllegalArgumentException("usage: no arguments");
        }
        listMonitoredResources();
        break;
      case "get-resource":
        args = commandLine.split("\\s+", 2);
        if (args.length != 2) {
          throw new IllegalArgumentException("usage: <type>");
        }
        describeMonitoredResources(args[1]);
        break;
      case "delete-metric-descriptor":
        args = commandLine.split("\\s+", 2);
        if (args.length != 2) {
          throw new IllegalArgumentException("usage: <type>");
        }
        deleteMetricDescriptor(args[1]);
        break;
      case "write-time-series":
        args = commandLine.split("\\s+", 2);
        if (args.length != 1) {
          throw new IllegalArgumentException("usage: no arguments");
        }
        writeTimeSeries();
        break;
      case "list-time-series-header":
        args = commandLine.split("\\s+", 2);
        if (args.length != 1) {
          throw new IllegalArgumentException("usage: no arguments");
        }
        listTimeSeriesHeaders();
        break;
      case "list-time-series":
        args = commandLine.split("\\s+", 2);
        if (args.length != 2) {
          throw new IllegalArgumentException("usage: <filter>");
        }
        listTimeSeries(args[1]);
        break;
      case "list-aggregate":
        args = commandLine.split("\\s+", 2);
        if (args.length != 1) {
          throw new IllegalArgumentException("usage: no arguments");
        }
        listTimeSeriesAggregrate();
        break;
      case "list-reduce":
        args = commandLine.split("\\s+", 2);
        if (args.length != 1) {
          throw new IllegalArgumentException("usage: no arguments");
        }
        listTimeSeriesReduce();
        break;
      default:
        throw new IllegalArgumentException("unrecognized command: " + command);
    }
  }

  private static void printUsage() {
    System.out.println("Usage:");
    System.out.println();
    System.out.println("  new-metric-descriptor Creates a metric descriptor");
    System.out.println("  list-metric-descriptors  Lists first page of metric descriptors");
    System.out.println("  list-monitored-resources Lists the monitored resources");
    System.out.println("  get-resource Describes a monitored resource");
    System.out.println("  delete-metric-descriptors  Deletes a metric descriptor");
    System.out.println("  write-time-series  Writes a time series value to a metric");
    System.out.println("  list-headers <filter> List time series header of "
        + " 'compute.googleapis.com/instance/cpu/utilization'");
    System.out.println("  list-time-series-header <filter> List time series data that matches a "
        + "given filter");
    System.out.println("  list-aggregate `Aggregates time series data that matches"
        + "'compute.googleapis.com/instance/cpu/utilization");
    System.out.println("  list-reduce `Reduces time series data that matches"
        + " 'compute.googleapis.com/instance/cpu/utilization");
    System.out.println();
  }

}
