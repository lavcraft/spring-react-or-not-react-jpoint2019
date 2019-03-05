package com.naya.gameofthrones.lettergrabber.controllers;

import com.naya.gameofthrones.lettergrabber.producer.LetterProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Evgeny Borisov
 */
@RestController
@RequiredArgsConstructor
public class LetterProducerConfiguratorController {
    private final LetterProducer producer;
    @GetMapping("/producer/producerDelay/{producerDelay}")
    public void setProducerDelay(@PathVariable("producerDelay") int delay) {
        producer.setDelay(delay);
    }

    @GetMapping("/timeToHandle/{timeToHandle}")
    public void setTimeToHandle(@PathVariable int timeToHandle){
        producer.setTimeToProcess(timeToHandle);
    }
}
