package com.naya.speedadjuster;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Evgeny Borisov
 */
@Data
@ConfigurationProperties("datasender")
public class AdjustmentProperties {
    private String url;
    private int    letterBoxSize                   = 100;
    private int    letterProcessorConcurrencyLevel = 1;
    private int    slowMultiplier                  = 1;

    private AtomicInteger request = new AtomicInteger(0);
}
