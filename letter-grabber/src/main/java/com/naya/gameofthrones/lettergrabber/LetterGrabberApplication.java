package com.naya.gameofthrones.lettergrabber;

import com.codahale.metrics.MetricRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LetterGrabberApplication {

    public static void main(String[] args) {
        SpringApplication.run(LetterGrabberApplication.class, args);
    }

}

