package ru.spring.demo.reactive.starter.speed.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.EmitterProcessor;
import ru.spring.demo.reactive.starter.speed.AdjustmentProperties;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class RequestController {
    private final AtomicInteger                          remainginRequests;
    private final AdjustmentProperties                   adjustmentProperties;
    private final ObjectProvider<EmitterProcessor<Long>> lettersProcessor;

    public RequestController(AdjustmentProperties adjustmentProperties,
                             ObjectProvider<EmitterProcessor<Long>> lettersProcessor) {
        this.remainginRequests = adjustmentProperties.getRequest();
        this.adjustmentProperties = adjustmentProperties;
        this.lettersProcessor = lettersProcessor;
    }

    @GetMapping("/request/{request}")
    public void request(@PathVariable int request) {
        lettersProcessor.ifAvailable(longEmitterProcessor -> {
            log.info("request controller {}", request);
            longEmitterProcessor.onNext((long) request);
        });

        remainginRequests.addAndGet(request);
    }

    @GetMapping("/request")
    public int getAtomicInteger() {
        return remainginRequests.get();
    }

    @GetMapping("/speed/{level}")
    public String setSpeed(@PathVariable int level) {
        adjustmentProperties.setSlowMultiplier(level);

        return "{ \"status\": \"ok\"}";
    }
}

