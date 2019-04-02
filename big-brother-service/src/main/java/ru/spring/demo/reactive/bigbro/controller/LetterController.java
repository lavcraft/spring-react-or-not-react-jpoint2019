package ru.spring.demo.reactive.bigbro.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.spring.demo.reactive.bigbro.services.GuardService;
import ru.spring.demo.reactive.bigbro.services.LetterDecoder;
import ru.spring.demo.reactive.starter.speed.AdjustmentProperties;
import ru.spring.demo.reactive.starter.speed.model.Letter;
import ru.spring.demo.reactive.starter.speed.services.LetterRequesterService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/analyse/letter")
public class LetterController {

    private final LetterDecoder           decoder;
    private final LetterRequesterService  letterRequesterService;
    private final BlockingQueue<Runnable> workingQueue;
    private final ThreadPoolExecutor      letterProcessorExecutor;
    private final GuardService            guardService;
    private final Counter                 counter;

    private final AtomicInteger guardRemainingRequest;

    public LetterController(LetterDecoder decoder,
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
        if(workingQueue.size() == 0) {
            letterRequesterService.request(letterProcessorExecutor.getMaximumPoolSize());
        }
    }

    //    @Async("letterProcessorExecutor")
    @PostMapping(consumes = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public void processLetter(@RequestBody Letter letterFlux) {
//        int parallelism = letterProcessorExecutor.getMaximumPoolSize();
//        letterFlux
//                .onBackpressureDrop(droppedLetter -> log.info("Drop letter {}", droppedLetter))
//                .doOnRequest(value -> {
//                    log.info("request({})", value);
//                    if(workingQueue.size() == 0) {
//                        letterRequesterService.request((int) value);
//                    }
//                })
////                .parallel(parallelism, parallelism + 20)
////                .runOn(Schedulers.fromExecutor(letterProcessorExecutor), parallelism + 20)
//                .flatMap(
//                        letter -> Mono.fromCallable(() -> decoder.decode(letter))
//                                .subscribeOn(Schedulers.fromExecutor(letterProcessorExecutor)),
//                        parallelism, parallelism)
//                .subscribe(letter -> {
////                    DecodedLetter decodedLetter = decoder.decode(letter);
//                    log.info("Decoded letter {}", letter);
//                    counter.increment();
////                    guardService.send(decodedLetter);
//                });
    }

}
