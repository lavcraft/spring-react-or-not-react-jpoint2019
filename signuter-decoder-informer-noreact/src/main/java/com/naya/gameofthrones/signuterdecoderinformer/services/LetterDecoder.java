package com.naya.gameofthrones.signuterdecoderinformer.services;

import com.naya.gameofthrones.signuterdecoderinformer.model.DecodedLetter;
import com.naya.speedadjuster.mode.Letter;

/**
 * @author Evgeny Borisov
 */
public interface LetterDecoder {
    DecodedLetter decode(Letter letter);
}
