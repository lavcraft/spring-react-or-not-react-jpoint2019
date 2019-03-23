package com.naya.gameofthrones.signuterdecoderinformer.controllers;

import com.naya.gameofthrones.signuterdecoderinformer.model.DecodedLetter;
import com.naya.gameofthrones.signuterdecoderinformer.model.Letter;
import com.naya.gameofthrones.signuterdecoderinformer.services.GuardService;
import com.naya.gameofthrones.signuterdecoderinformer.services.LetterDecoder;
import com.naya.speedadjuster.AdjustmentProperties;
import com.naya.speedadjuster.services.LetterRequesterService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@RestController
@RequestMapping("/analyse/letter")
public class LetterReceiverController {
    private final LetterDecoder           decoder;
    private final LetterRequesterService  letterRequesterService;
    private final BlockingQueue<Runnable> workingQueue;
    private final ThreadPoolExecutor      letterProcessorExecutor;
    private final GuardService            guardService;
    private final Counter                 counter;

    private final AtomicInteger guardRemainingRequest;

    public LetterReceiverController(LetterDecoder decoder,
                                    LetterRequesterService letterRequesterService,
                                    GuardService guardService,
                                    MeterRegistry meterRegistry,
                                    AdjustmentProperties adjustmentProperties,
                                    ThreadPoolExecutor letterProcessorExecutor) {
        this.decoder = decoder;
        this.letterRequesterService = letterRequesterService;
        this.guardService = guardService;
        this.letterProcessorExecutor = letterProcessorExecutor;

        counter = meterRegistry.counter("letter.rps");
        workingQueue = letterProcessorExecutor.getQueue();
        guardRemainingRequest = adjustmentProperties.getRequest();
    }

    @Scheduled(fixedDelay = 300)
    public void init() {
        if(workingQueue.size() == 0 && guardRemainingRequest.get() > 0) {
            letterRequesterService.request(letterProcessorExecutor.getMaximumPoolSize());
        }
    }

    @PostMapping
    @Async("letterProcessorExecutor")
    public void processLetter(@RequestBody Letter letter) {
        DecodedLetter decode = decoder.decode(letter);
        counter.increment();

        guardRemainingRequest.getAndAccumulate(1, (prev, delta) -> {
            guardService.send(decode);
            int remaining = prev - delta;
            return remaining < 0 ? 0 : remaining;
        });
    }
}
