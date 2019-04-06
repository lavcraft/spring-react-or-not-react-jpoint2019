package ru.spring.demo.reactive.bigbro;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@Slf4j
@EnableAsync
@EnableWebFlux
@EnableScheduling
@SpringBootApplication
public class BigBrotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigBrotherApplication.class, args);
    }

}

