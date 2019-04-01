package com.naya.gameofthrones.lettergrabber.producer;

import com.github.javafaker.GameOfThrones;
import com.naya.speedadjuster.AdjustmentProperties;
import com.naya.speedadjuster.mode.Letter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@Service
@Setter
public class LetterProducerFake implements LetterProducer {
    private final GameOfThrones      faker;
    private final ThreadPoolExecutor letterProcessorExecutor;
    private final AtomicInteger      remainingRequest;

    private ObjectProvider<EmitterProcessor<Long>> unicastProcessor;

    public LetterProducerFake(GameOfThrones faker,
                              ThreadPoolExecutor letterProcessorExecutor,
                              AdjustmentProperties adjustmentProperties,
                              ObjectProvider<EmitterProcessor<Long>> unicastProcessorOptional) {
        this.faker = faker;
        this.letterProcessorExecutor = letterProcessorExecutor;
        this.remainingRequest = adjustmentProperties.getRequest();
        this.unicastProcessor = unicastProcessorOptional;
    }

    @Override
    @SneakyThrows
    public Letter getLetter() {
        return randomLetter();
    }

    LinkedBlockingQueue letterQueue = new LinkedBlockingQueue();

    @Override
    public Flux<Letter> letterFlux() {
        return Flux.generate(synchronousSink -> synchronousSink.next(randomLetter()));
    }

    private Letter randomLetter() {
        return Letter.builder()
                .content(faker.quote())
                .location(faker.city())
                .signature(faker.character())
                .build();
    }


}
