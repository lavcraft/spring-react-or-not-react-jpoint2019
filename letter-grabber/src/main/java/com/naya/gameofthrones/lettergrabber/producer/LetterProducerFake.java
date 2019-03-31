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
        return Flux.<Letter>generate(synchronousSink -> synchronousSink.next(randomLetter()))
                .doOnRequest(value -> log.info("from consumer {}", value))
                .transform(Operators.<Letter, Letter>liftPublisher((ignore, downstream) -> new BaseSubscriber<Letter>() { //remove letter and discuss about compiler bug. Or not bug, its a question
                            @Override
                            protected void hookOnSubscribe(Subscription subscription) {
                                EmitterProcessor<Long> ifAvailable = unicastProcessor.getIfAvailable();
                                if(ifAvailable != null) {
                                    ifAvailable.subscribe(subscription::request);
                                }

                                downstream.onSubscribe(new Subscription() {
                                    @Override
                                    public void request(long n) {
                                        log.info("from network request {} ", n);
                                    }

                                    @Override
                                    public void cancel() {
                                        subscription.cancel();
                                    }
                                });
                            }

                            @Override
                            protected void hookOnNext(Letter value) {
                                downstream.onNext(value);
                            }

                            @Override
                            protected void hookOnComplete() {
                                downstream.onComplete();
                            }

                            @Override
                            protected void hookOnError(Throwable throwable) {
                                downstream.onError(throwable);
                            }
                        })
                );
    }

    private Letter randomLetter() {
        return Letter.builder()
                .content(faker.quote())
                .location(faker.city())
                .signature(faker.character())
                .build();
    }


}
