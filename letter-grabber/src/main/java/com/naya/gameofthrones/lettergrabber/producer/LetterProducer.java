package com.naya.gameofthrones.lettergrabber.producer;

import com.naya.gameofthrones.lettergrabber.model.Letter;
import lombok.SneakyThrows;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author Evgeny Borisov
 */
public interface LetterProducer {
    Letter getLetter();
    Flux<Letter> letterFlux();
}
