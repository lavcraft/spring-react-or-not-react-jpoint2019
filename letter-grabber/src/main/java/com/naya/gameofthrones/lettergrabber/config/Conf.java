package com.naya.gameofthrones.lettergrabber.config;

import com.github.javafaker.Faker;
import com.github.javafaker.GameOfThrones;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Evgeny Borisov
 */
@Configuration
public class Conf {
    @Bean
    public GameOfThrones faker(){
        return new Faker().gameOfThrones();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
