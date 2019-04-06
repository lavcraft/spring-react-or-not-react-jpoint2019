package ru.spring.demo.reactive.smith.notifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.spring.demo.reactive.starter.speed.model.Notification;


@Slf4j
@Service
@RequiredArgsConstructor
public class Notifier {
    private final RestTemplate restTemplate;

    public void sendNotification(Notification notification) {
        try {
            restTemplate.postForObject("http://localhost:8080/letter-status", notification, ResponseEntity.class);
            log.info("Guard notification sent");
        } catch (Exception e) {
            log.error("no sender url found");
        }
    }
}
