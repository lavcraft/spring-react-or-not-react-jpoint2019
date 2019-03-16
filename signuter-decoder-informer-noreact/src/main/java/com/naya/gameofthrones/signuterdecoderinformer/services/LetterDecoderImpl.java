package com.naya.gameofthrones.signuterdecoderinformer.services;

import com.naya.gameofthrones.signuterdecoderinformer.model.DecodedLetter;
import com.naya.gameofthrones.signuterdecoderinformer.model.Letter;
import com.naya.speedadjuster.AdjustmentProperties;
import com.naya.speedadjuster.services.LetterRequesterService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class LetterDecoderImpl implements LetterDecoder {
    private final AdjustmentProperties   adjustmentProperties;

    @Override
    @SneakyThrows
    public DecodedLetter decode(Letter letter) {
        SECONDS.sleep(
                round(adjustmentProperties.getSlowMultiplier())
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
