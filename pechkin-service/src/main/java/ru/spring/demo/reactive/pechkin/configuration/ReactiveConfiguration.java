package ru.spring.demo.reactive.pechkin.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.EmitterProcessor;

@Configuration
public class ReactiveConfiguration {
    @Bean
    public EmitterProcessor<Long> unicastProcessorLetter() {
        return EmitterProcessor.create(false);
    }
}
