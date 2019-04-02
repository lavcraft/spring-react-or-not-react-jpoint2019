package ru.spring.demo.reactive.bigbro.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.spring.demo.reactive.starter.speed.model.DecodedLetter;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuardService {
    private final RestTemplate restTemplate;

    @Async
    public void send(DecodedLetter decodedLetter) {
        try {
            restTemplate.postForObject("http://localhost:8082/guard", decodedLetter, Void.class);
        } catch (RestClientException e) {
            log.error("cant send action to guard service error", e);
        }
    }
}
