package com.naya.gameofthrones.signuterdecoderinformer.controllers;

import com.naya.gameofthrones.signuterdecoderinformer.model.DecodedLetter;
import com.naya.gameofthrones.signuterdecoderinformer.model.Letter;
import com.naya.gameofthrones.signuterdecoderinformer.services.LetterDecoder;
import com.naya.speedadjuster.annotations.Balanced;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Evgeny Borisov
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/analyse/letter")
@Slf4j
public class LetterReceiverController {
    private final LetterDecoder decoder;

    @PostMapping
    @Balanced
    public void processLetter(@RequestBody Letter letter) {
        DecodedLetter decodedLetter = decoder.decode(letter);
        log.info(decodedLetter + " was sent to police");
    }
}
