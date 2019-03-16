package com.naya.gameofthrones.signuterdecoderinformer.services;

import com.naya.gameofthrones.signuterdecoderinformer.model.DecodedLetter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GuardService {
    private final RestTemplate restTemplate;

    @Async
    public void send(DecodedLetter decodedLetter) {
        GuardRequest request = new GuardRequest()
                .setLetterId(decodedLetter.getAuthor())
                .setMessage(decodedLetter.getContent());

        restTemplate.postForObject("http://localhost:8082/guard", request, Void.class);
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class GuardRequest {
        private String letterId;
        private String message;
    }
}
