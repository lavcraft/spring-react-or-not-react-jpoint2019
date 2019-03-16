package com.naya.speedadjuster.configuration;

import com.naya.speedadjuster.AdjustmentProperties;
import com.naya.speedadjuster.services.LetterRequesterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@Configuration
@EnableScheduling
@EnableConfigurationProperties(AdjustmentProperties.class)
public class SpeedAdjusterConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public LetterRequesterService letterRequesterService(AdjustmentProperties properties,
                                                         RestTemplate restTemplate) {
        return new LetterRequesterService(restTemplate, properties);
    }

    @Bean
    public ThreadPoolExecutor letterProcessorExecutor(AdjustmentProperties adjustmentProperties) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                adjustmentProperties.getLetterProcessorConcurrencyLevel(),
                adjustmentProperties.getLetterProcessorConcurrencyLevel(),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(10));

        threadPoolExecutor.setRejectedExecutionHandler((r, executor) -> {
            log.info("task failed — {}", r);
        });
        threadPoolExecutor.prestartAllCoreThreads();
        return threadPoolExecutor;
    }
}
