package com.naya.gameofthrones.lettergrabber.producer;

import com.naya.speedadjuster.mode.Letter;
import reactor.core.publisher.Flux;

/**
 * @author Evgeny Borisov
 */
public interface LetterProducer {
    Letter getLetter();
    Flux<Letter> letterFlux();
}
