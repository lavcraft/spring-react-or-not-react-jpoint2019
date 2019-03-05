package com.naya.gameofthrones.lettergrabber.services;

import com.naya.gameofthrones.lettergrabber.model.Letter;

/**
 * @author Evgeny Borisov
 */
public interface LetterSender {
    void send(Letter letter);
}
