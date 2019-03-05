package com.naya.gameofthrones.lettergrabber.producer;

import com.naya.gameofthrones.lettergrabber.model.Letter;
import lombok.SneakyThrows;

import java.util.List;

/**
 * @author Evgeny Borisov
 */
public interface LetterProducer {
    Letter getLetter();

    void setDelay(int delay);

    void setTimeToProcess(int timeToProcess);
}
