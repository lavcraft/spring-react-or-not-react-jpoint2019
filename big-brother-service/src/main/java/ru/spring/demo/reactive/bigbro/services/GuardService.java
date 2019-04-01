package ru.spring.demo.reactive.bigbro.services;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.spring.demo.reactive.bigbro.model.DecodedLetter;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuardService {
    private final RestTemplate restTemplate;

    @Async
    public void send(DecodedLetter decodedLetter) {
        GuardRequest request = new GuardRequest()
                .setLetterId(decodedLetter.getAuthor())
                .setMessage(decodedLetter.getContent());

        try {
            restTemplate.postForObject("http://localhost:8082/guard", request, Void.class);
        } catch (RestClientException e) {
            log.error("cant send message to guard service error", e);
        }
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class GuardRequest {
        private String letterId;
        private String message;
    }
}
