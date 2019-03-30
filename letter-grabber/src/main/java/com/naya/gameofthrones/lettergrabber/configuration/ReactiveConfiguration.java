package com.naya.gameofthrones.lettergrabber.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.UnicastProcessor;

@Configuration
public class ReactiveConfiguration {
    @Bean
    public EmitterProcessor<Long> unicastProcessorLetter() {
        return EmitterProcessor.create();
    }
}
