package com.naya.speedadjuster;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Evgeny Borisov
 */
@Data
@ConfigurationProperties("datasender")
public class AdjustmentProperties {
    private String url;
    private int    numberOfThreads = 1;
    private int    slowMultiplier  = 1;
}
