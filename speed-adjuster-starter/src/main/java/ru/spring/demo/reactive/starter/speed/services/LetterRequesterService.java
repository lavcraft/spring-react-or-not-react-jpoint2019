package ru.spring.demo.reactive.starter.speed.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.spring.demo.reactive.starter.speed.AdjustmentProperties;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class LetterRequesterService {
    private final RestTemplate         restTemplate;
    private final AdjustmentProperties adjustmentProperties;

    public void request(int n) {
        syncSpeed(n);
    }

    public void request() {
        syncSpeed(adjustmentProperties.getSlowMultiplier() * adjustmentProperties.getLetterProcessorConcurrencyLevel());
    }

    private void syncSpeed(int n) {
        try {
            restTemplate.getForObject(
                    adjustmentProperties.getUrl() + "/" + n,
                    Void.class);
        } catch (RestClientException e) {
            log.error("no sender url found", e);
        }
    }
}
