package ru.spring.demo.reactive.pechkin.producer;

import com.github.javafaker.RickAndMorty;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Operators;
import ru.spring.demo.reactive.starter.speed.AdjustmentProperties;
import ru.spring.demo.reactive.starter.speed.model.Letter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@Service
@Setter
@RequiredArgsConstructor
public class LetterProducer {
    private final RickAndMorty                           faker;
    private final ThreadPoolExecutor                     letterProcessorExecutor;
    private final ObjectProvider<EmitterProcessor<Long>> emitterProcessorProvider;
    private final AdjustmentProperties                   adjustmentProperties;

    @SneakyThrows
    public Letter getLetter() {
        return randomLetter();
    }

    LinkedBlockingQueue letterQueue = new LinkedBlockingQueue();

    static int a = 0;
    public Flux<Letter> letterFlux() {
//        return Flux.create(fluxSink -> {
//            EmitterProcessor<Long> ifAvailable = emitterProcessorProvider.getIfAvailable();
//            ifAvailable.subscribe(requested -> {
//                for (int i = 0; i < requested; i++) {
//                    fluxSink.next(randomLetter());
//                }
//            });
//        });

//        return Flux.<Letter>generate(letterSynchronousSink -> letterSynchronousSink.next(randomLetter()))
        return Flux.<Letter>generate(synchronousSink -> synchronousSink.next(randomLetter()))
                .doOnRequest(value -> log.info("from consumer {}", value))
                .<Letter>transform(Operators.lift((ignore, downstream) -> new BaseSubscriber<Letter>() { //remove letter and discuss about compiler bug. Or not bug, its a question
                            @Override
                            protected void hookOnSubscribe(Subscription subscription) {
                                EmitterProcessor<Long> emitterProcessorReal = emitterProcessorProvider.getIfAvailable();
                                if(emitterProcessorReal != null) {
                                    emitterProcessorReal.subscribe(subscription::request);
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

//        return Flux.generate(synchronousSink -> synchronousSink.next(randomLetter()));
    }

    private Letter randomLetter() {
        String character = faker.character();
        return Letter.builder()
                .content(faker.quote())
                .location(faker.location())
                .signature(character)
                ._original(character)
                .build();
    }


}
