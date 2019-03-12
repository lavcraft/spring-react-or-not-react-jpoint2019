package ru.spring.demo.reactive.metrics;

import com.codahale.metrics.MetricRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Endpoint(id = "rates")
@RequiredArgsConstructor
public class RatesMetricsEndpoint {
    private final MetricRegistry metricRegistry;

    @ReadOperation
    public Map<String, Double> allrates() {
        return metricRegistry.getMeters().entrySet().stream()
                .map(stringMeterEntry -> Map.entry(stringMeterEntry.getKey(), stringMeterEntry.getValue().getOneMinuteRate()))
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (o, o2) -> o
                        )
                );
    }

    @ReadOperation
    public Map<String, Double> rates(@Selector String arg0) {
        return metricRegistry.getMeters().entrySet().stream()
                .filter(stringMeterEntry -> stringMeterEntry.getKey().contains(arg0))
                .map(stringMeterEntry -> Map.entry(stringMeterEntry.getKey(), stringMeterEntry.getValue().getOneMinuteRate()))
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (o, o2) -> o
                        )
                );
    }
}
