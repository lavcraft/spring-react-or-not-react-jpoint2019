package com.naya.gameofthrones.lettergrabber.services;

import lombok.SneakyThrows;

/**
 * @author Evgeny Borisov
 */
public interface LetterDistributor {
    void distribute();

    void request(int request);

    int getRemainingRequestCount();
}
