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

import java.util.List;
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
    private final GameOfThrones faker;

    @Override
    @SneakyThrows
    public Letter getLetter() {
        return randomLetter();
    }

    @Override
    public Flux<Letter> letterFlux() {
        return Flux.generate(sink -> sink.next(randomLetter()));
    }

    private Letter randomLetter() {
        return Letter.builder()
                .content(faker.quote())
                .location(faker.city())
                .signature(faker.character())
                .build();
    }


}
