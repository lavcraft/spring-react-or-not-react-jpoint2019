package com.naya.gameofthrones.signuterdecoderinformer.services;

import com.naya.gameofthrones.signuterdecoderinformer.model.DecodedLetter;
import com.naya.speedadjuster.mode.Letter;
import com.naya.speedadjuster.AdjustmentProperties;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.lang.Math.round;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Evgeny Borisov
 */
@Service
@Slf4j
public class LetterDecoderImpl implements LetterDecoder {
    private final AdjustmentProperties adjustmentProperties;
    private final Counter counter;

    public LetterDecoderImpl(AdjustmentProperties adjustmentProperties,
                             MeterRegistry meterRegistry) {
        this.adjustmentProperties = adjustmentProperties;
        counter = meterRegistry.counter("letter.rps");
    }


    @Override
    @SneakyThrows
    public DecodedLetter decode(Letter letter) {
        SECONDS.sleep(
                round(adjustmentProperties.getSlowMultiplier())
        );

        counter.increment();

        //TODO add reflection
        String author = letter.getSignature();

        return DecodedLetter.builder()
                .author(author)
                .location(letter.getLocation())
                .content(letter.getContent())
                .build();

    }
}
