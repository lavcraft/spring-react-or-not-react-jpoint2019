package ru.spring.demo.reactive.dashboard.console.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class RateStatus {
    private String component;
    private double letterRps;

    @JsonProperty("buffer.size")
    private int bufferSize;

    @JsonProperty("buffer.capacity")
    private int bufferCapacity;

    private List<BufferStatus> buffers;

    @Data
    public static class BufferStatus {
        private int remaining;
        private int maxSize;
        private int activeWorker;
        private int workersCount;
    }
}
