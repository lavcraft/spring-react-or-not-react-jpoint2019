package ru.spring.demo.reactive.pechkin.producer;

import com.github.javafaker.GameOfThrones;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import ru.spring.demo.reactive.starter.speed.mode.Letter;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@Service
@Setter
@RequiredArgsConstructor
public class LetterProducer {
    private final GameOfThrones                          faker;
    private final ObjectProvider<EmitterProcessor<Long>> unicastProcessor;


    @SneakyThrows
    public Letter getLetter() {
        return randomLetter();
    }

    LinkedBlockingQueue letterQueue = new LinkedBlockingQueue();

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
