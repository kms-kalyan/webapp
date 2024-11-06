package com.csye6225.cloud.webapp.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

@Service
public class MetricsService {

    private final CloudWatchClient cloudWatchClient;

    public MetricsService(CloudWatchClient cloudWatchClient) {
        this.cloudWatchClient = cloudWatchClient;
    }

    @SuppressWarnings("unchecked")
    public void recordApiCall(String apiName) {
        MetricDatum datum = MetricDatum.builder()
            .metricName("ApiCallCount")
            .dimensions(d -> d.name("API").value(apiName))
            .unit(StandardUnit.COUNT)
            .value(1.0)
            .build();

        PutMetricDataRequest request = PutMetricDataRequest.builder()
            .namespace("CSYE6225/WebApp")
            .metricData(datum)
            .build();

        cloudWatchClient.putMetricData(request);
    }
    
    @SuppressWarnings("unchecked")
    public void recordApiDuration(String apiName, double durationMillis) {
        MetricDatum datum = MetricDatum.builder()
            .metricName("ApiDuration")
            .dimensions(d -> d.name("API").value(apiName))
            .unit(StandardUnit.MILLISECONDS)
            .value(durationMillis)
            .build();

        PutMetricDataRequest request = PutMetricDataRequest.builder()
            .namespace("CSYE6225/WebApp")
            .metricData(datum)
            .build();

        cloudWatchClient.putMetricData(request);
    }
}