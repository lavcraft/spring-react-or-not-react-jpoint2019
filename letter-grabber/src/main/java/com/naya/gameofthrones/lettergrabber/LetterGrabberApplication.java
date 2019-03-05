package com.naya.gameofthrones.lettergrabber;

import com.naya.gameofthrones.lettergrabber.services.LetterDistributor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LetterGrabberApplication {

    public static void main(String[] args) {
        SpringApplication.run(LetterGrabberApplication.class, args);
    }

}

