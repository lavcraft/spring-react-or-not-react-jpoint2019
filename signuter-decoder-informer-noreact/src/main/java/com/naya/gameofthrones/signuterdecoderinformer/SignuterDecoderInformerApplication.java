package com.naya.gameofthrones.signuterdecoderinformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class SignuterDecoderInformerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignuterDecoderInformerApplication.class, args);
    }

}

