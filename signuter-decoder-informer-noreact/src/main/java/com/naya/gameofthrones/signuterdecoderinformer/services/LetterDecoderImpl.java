package com.naya.gameofthrones.signuterdecoderinformer.services;

import com.naya.gameofthrones.signuterdecoderinformer.model.DecodedLetter;
import com.naya.gameofthrones.signuterdecoderinformer.model.Letter;
import com.naya.speedadjuster.AdjustmentProperties;
import com.naya.speedadjuster.services.LetterRequesterService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.round;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Evgeny Borisov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LetterDecoderImpl implements LetterDecoder {
    private final AdjustmentProperties adjustmentProperties;

    private final ConcurrentLinkedQueue<Letter> letters = new ConcurrentLinkedQueue<>();

    @Override
    @SneakyThrows
    public DecodedLetter decode(Letter letter) {
        MILLISECONDS.sleep(
                round(adjustmentProperties.getSlowMultiplier() * 1000 / adjustmentProperties.getNumberOfThreads())
        );

        //TODO add reflection
        String author = letter.getSignature();

        return DecodedLetter.builder()
                .author(author)
                .location(letter.getLocation())
                .content(letter.getContent())
                .build();
    }
}
