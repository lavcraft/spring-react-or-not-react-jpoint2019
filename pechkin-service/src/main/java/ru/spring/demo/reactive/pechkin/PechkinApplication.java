package ru.spring.demo.reactive.pechkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PechkinApplication {
    public static void main(String[] args) {
        SpringApplication.run(PechkinApplication.class, args);
    }
}

