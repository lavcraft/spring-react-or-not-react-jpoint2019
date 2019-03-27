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
import org.reactivestreams.Subscription;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON_VALUE;

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

    //    @Async("letterProcessorExecutor")
    @PostMapping(consumes = APPLICATION_STREAM_JSON_VALUE)
    public Mono<Void> processLetter(@RequestBody Flux<Letter> letterFlux) {
        int prefetch = letterFlux.getPrefetch();
        letterFlux
                .onBackpressureDrop(droppedLetter -> log.info("Drop letter {}", droppedLetter))
                .doOnRequest(value -> {
                    //request.send
                })
                .parallel(letterProcessorExecutor.getMaximumPoolSize()+1)
                .runOn(Schedulers.fromExecutor(letterProcessorExecutor))
                .subscribe(letter -> {
                    DecodedLetter decodedLetter = decoder.decode(letter);
                    log.info("Decoded letter {}", decodedLetter);
                    guardService.send(decodedLetter);
                });
//                .publish(workerFlux -> letterFlux
//                        .parallel(letterProcessorExecutor.getMaximumPoolSize())
//                        .runOn(Schedulers.fromExecutor(letterProcessorExecutor))
//                        .map(letter -> decoder.decode(letter))
//                        .doOnNext(decodedLetter -> log.info("Decoded letter {}", decodedLetter)));
//                .then();

        return Mono.never();

//        DecodedLetter decode = decoder.decode(letter);
//        counter.increment();
//
//        guardRemainingRequest.getAndAccumulate(1, (prev, delta) -> {
//            guardService.send(decode);
//            int remaining = prev - delta;
//            return remaining < 0 ? 0 : remaining;
//        });
    }
}
