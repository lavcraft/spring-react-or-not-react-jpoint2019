package com.naya.gameofthrones.lettergrabber.producer;

import com.github.javafaker.GameOfThrones;
import com.naya.gameofthrones.lettergrabber.model.Letter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@Service
@Setter
@RequiredArgsConstructor
public class LetterProducerFake implements LetterProducer {
    private final GameOfThrones      faker;
    private final ThreadPoolExecutor letterProcessorExecutor;

    @Override
    @SneakyThrows
    public Letter getLetter() {
        return randomLetter();
    }

    LinkedBlockingQueue letterQueue = new LinkedBlockingQueue();

    @Override
    public Flux<Letter> letterFlux() {
//        Flux.create(fluxSink -> {
//            while (true) {
//                if(letterQueue.size() != 0) {
//                    fluxSink.next(letterQueue.peek());
//                } else {
//                    fluxSink.next(getLetter());
//                }
//            }
//        })
//                .onBackpressureDrop(o -> {});

//        return Flux.generate(letterSynchronousSink -> letterSynchronousSink.next(randomLetter()));
        return Flux.interval(Duration.ofMillis(10))
                .publishOn(Schedulers.fromExecutor(letterProcessorExecutor))
                .map(aLong -> randomLetter());
    }

    private Letter randomLetter() {
        return Letter.builder()
                .content(faker.quote())
                .location(faker.city())
                .signature(faker.character())
                .build();
    }


}
