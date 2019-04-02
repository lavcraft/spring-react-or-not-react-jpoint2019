package ru.spring.demo.reactive.pechkin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.spring.demo.reactive.starter.speed.model.Letter;

@Service
@RequiredArgsConstructor
@Slf4j
public class LetterSender {
    private final RestTemplate restTemplate;

    public void send(Letter letter) {
        try {
            restTemplate.postForEntity("http://localhost:8081/analyse/letter", letter, Void.class);
        } catch (RestClientException e) {
            log.error("no letter analyzer found", e);
        }
    }
}
