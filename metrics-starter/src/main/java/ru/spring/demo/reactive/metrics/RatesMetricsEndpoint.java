package ru.spring.demo.reactive.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Endpoint(id = "rates")
public class RatesMetricsEndpoint {
    private static final Collector<Entry<String, Object>, ?, Map<String, Object>> ENTRY_MAP_COLLECTOR = toMap(
            Entry::getKey,
            Entry::getValue,
            (o, o2) -> {
                if(o instanceof List && o2 instanceof List) {
                    ((List) o).addAll((Collection) o2);
                }
                return o;
            }
    );

    private @Autowired MetricRegistry                            metricRegistry;
    private @Autowired Optional<Map<String, ThreadPoolExecutor>> executors;

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
        return executors
                .map(Map::entrySet)
                .map(Collection::stream)
                .map(threadPoolExecutorStream -> threadPoolExecutorStream
                        .map(entry -> extractThreadPoolStats(entry.getKey(), entry.getValue())))
                .map(mapStream -> mapStream
                        .map(Map::entrySet)
                        .flatMap(Collection::stream)
                        .collect(ENTRY_MAP_COLLECTOR)
                ).orElse(Collections.emptyMap());
    }

    private Map<String, Object> extractMeterRates(Stream<Entry<String, Meter>> stream) {
        return stream
                .map(metric -> entry(metric.getKey(), (Object) metric.getValue().getOneMinuteRate()))
                .collect(ENTRY_MAP_COLLECTOR);
    }

    private Map<String, Object> extractThreadPoolStats(String name, ThreadPoolExecutor threadPoolExecutor) {
        int remainingCapacity = threadPoolExecutor.getQueue().remainingCapacity();
        int queueSize         = threadPoolExecutor.getQueue().size() + remainingCapacity;
        int threadInWork      = threadPoolExecutor.getActiveCount();
        int maximumPoolSize   = threadPoolExecutor.getMaximumPoolSize();

        return new ImmutableMap.Builder<String, Object>()
                .put("buffers", Arrays.asList(
                        new BufferStats()
                                .setName(name)
                                .setRemaining(remainingCapacity)
                                .setMaxSize(queueSize)
                                .setActiveWorker(threadInWork)
                                .setWorkersCount(maximumPoolSize)
                        )
                )
                .put("buffer.size", remainingCapacity)
                .put("buffer.capacity", queueSize)
                .build();
    }

    @Data
    @Accessors(chain = true)
    public static class BufferStats {
        String name;
        int remaining;
        int maxSize;
        int activeWorker;
        int workersCount;
    }
}
