package ru.spring.demo.reactive.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.core.codec.StringDecoder;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Endpoint(id = "rates")
public class RatesMetricsEndpoint {
    private static final Collector<Entry<String, Object>, ?, Map<String, Object>> ENTRY_MAP_COLLECTOR = toMap(
            Entry::getKey,
            Entry::getValue,
            (o, o2) -> o
    );

    private @Autowired MetricRegistry                     metricRegistry;
    private @Autowired Optional<List<ThreadPoolExecutor>> letterProcessorExecutor;

    @ReadOperation
    public Map<String, Object> allrates() {
        return extractRatesFromMeters();
    }

    @ReadOperation
    public Map<String, Object> rates(@Selector String arg0) {
        Map<String, Object> rates = extractMeterRates(
                metricRegistry.getMeters().entrySet().stream()
                        .filter(stringMeterEntry -> stringMeterEntry.getKey().contains(arg0))
        );

        rates.putAll(extractPoolStats());

        return rates;
    }

    private Map<String, Object> extractRatesFromMeters() {
        Map<String, Object> rates = extractMeterRates(metricRegistry.getMeters().entrySet().stream());

        rates.putAll(extractPoolStats());

        return rates;
    }

    private Map<String, Object> extractPoolStats() {
        return letterProcessorExecutor
                .map(Collection::stream)
                .map(threadPoolExecutorStream -> threadPoolExecutorStream.map(this::extractThreadPoolStats))
                .map(mapStream -> mapStream
                        .map(Map::entrySet)
                        .flatMap(Collection::stream)
                        .collect(ENTRY_MAP_COLLECTOR)
                )
                .orElse(Collections.emptyMap());
    }

    private Map<String, Object> extractMeterRates(Stream<Entry<String, Meter>> stream) {
        return stream
                .map(metric -> entry(metric.getKey(), (Object) metric.getValue().getOneMinuteRate()))
                .collect(ENTRY_MAP_COLLECTOR);
    }

    private Map<String, Object> extractThreadPoolStats(ThreadPoolExecutor threadPoolExecutor) {
        int queueSize         = threadPoolExecutor.getQueue().size();
        int remainingCapacity = threadPoolExecutor.getQueue().remainingCapacity();

        return new ImmutableMap.Builder<String, Object>()
                .put("buffer.size", remainingCapacity)
                .put("buffer.capacity", queueSize)
                .build();
    }
}
