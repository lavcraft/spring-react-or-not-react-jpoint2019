package ru.spring.demo.reactive.pechkin.configuration;

import com.github.javafaker.Faker;
import com.github.javafaker.GameOfThrones;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Evgeny Borisov
 */
@Configuration
public class FakeDataConfiguration {
    @Bean
    public GameOfThrones faker(){
        return new Faker().gameOfThrones();
    }
}
