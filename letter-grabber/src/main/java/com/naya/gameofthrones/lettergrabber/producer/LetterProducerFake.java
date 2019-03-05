package com.naya.gameofthrones.lettergrabber.producer;

import com.github.javafaker.GameOfThrones;
import com.naya.gameofthrones.lettergrabber.model.Letter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Evgeny Borisov
 */
@Service
@Setter
@RequiredArgsConstructor
public class LetterProducerFake implements LetterProducer {
    private final GameOfThrones faker;

    private int delay                = 10;
    private int timeToProcess        = 1;

    @Override
    @SneakyThrows
    public Letter getLetter() {
        Thread.sleep(delay);
        return randomLetter();
    }

    private Letter randomLetter() {
        return Letter.builder()
                .content(faker.quote())
                .location(faker.city())
                .signature(faker.character())
                .timeToProcess(timeToProcess)
                .build();
    }


}
