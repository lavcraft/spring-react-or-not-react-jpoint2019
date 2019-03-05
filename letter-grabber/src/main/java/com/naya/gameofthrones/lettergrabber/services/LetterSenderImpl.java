package com.naya.gameofthrones.lettergrabber.services;

import com.naya.gameofthrones.lettergrabber.model.Letter;
import com.naya.gameofthrones.lettergrabber.producer.LetterProducer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author Evgeny Borisov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LetterSenderImpl implements LetterSender {
    private final RestTemplate restTemplate;



    public void send(Letter letter) {
        try {
            restTemplate.postForObject("http://localhost:8081/analyse/letter", letter, ResponseEntity.class);
        } catch (RestClientException e) {
            log.error("no letter analyzer found");
        }
        log.info(letter+" was sent");
    }
}
