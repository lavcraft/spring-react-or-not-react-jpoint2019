package com.naya.gameofthrones.lettergrabber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@SpringBootApplication
public class LetterGrabberApplication {
    public static void main(String[] args) {
        SpringApplication.run(LetterGrabberApplication.class, args);
    }
}

