package ru.spring.demo.reactive.dashboard.console.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
}
