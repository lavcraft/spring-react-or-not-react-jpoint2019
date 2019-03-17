package com.naya.gameofthrones.lettergrabber.services;

import com.naya.gameofthrones.lettergrabber.model.Letter;
import com.naya.gameofthrones.lettergrabber.producer.LetterProducer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@Service
public class LetterDistributorImpl implements LetterDistributor {
    private final LetterProducer producer;
    private final LetterSender   sender;

    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    private final Counter counter;

    public LetterDistributorImpl(LetterProducer producer,
                                 LetterSender sender,
                                 MeterRegistry meterRegistry) {
        this.producer = producer;
        this.sender = sender;
        this.counter = meterRegistry.counter("letterRps");
    }

    @Scheduled(fixedDelay = 500)
    public void send() {
        while (true) {
            try {
                if (atomicInteger.get() > 0) {
                    distribute();
                    counter.increment();
                }
            } catch (Exception e) {
                log.error("Cannot send letter");
                try {
                    SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @SneakyThrows
    @Override
    public void distribute() {
        Letter letter = producer.getLetter();
        //TODO add letter per seconds indicator
        sender.send(letter);
        atomicInteger.getAndDecrement();
    }

    @Override
    public void request(int request) {
        atomicInteger.addAndGet(request);
    }

    @Override
    public int getRemainingRequestCount() {
        return atomicInteger.get();
    }
}
