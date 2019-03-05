package com.naya.speedadjuster.services;

import com.naya.speedadjuster.AdjustmentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Evgeny Borisov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpeedAdjusterService {
    private final RestTemplate         restTemplate;
    private final AdjustmentProperties adjustmentProperties;

    @Scheduled(fixedDelay = 4000)
    public void request() {
        syncSpeed();
    }

    private void syncSpeed() {
        try {
            restTemplate.getForObject(
                    adjustmentProperties.getUrl() + "/" + adjustmentProperties.getSlowMultiplier() * adjustmentProperties.getNumberOfThreads(),
                    Void.class);
        } catch (RestClientException e) {
            log.error("no sender url found");
        }
    }
}
