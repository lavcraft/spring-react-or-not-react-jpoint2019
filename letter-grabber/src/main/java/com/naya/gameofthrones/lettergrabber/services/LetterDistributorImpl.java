package com.naya.gameofthrones.lettergrabber.services;

import com.naya.gameofthrones.lettergrabber.model.Letter;
import com.naya.gameofthrones.lettergrabber.producer.LetterProducer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Evgeny Borisov
 */
@Service
@RequiredArgsConstructor
public class LetterDistributorImpl implements LetterDistributor {
    private final LetterProducer producer;
    private final LetterSender   sender;

    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Scheduled(fixedRate = 10)
    public void send() {
        atomicInteger.getAndAccumulate(-1, (prev, newval) -> {
            try {
                if(prev >0 ) {
                    distribute();
                    return prev - 1;
                }
            } catch (Exception e) {
                return prev;
            }
            return 0;
        });
    }

    @SneakyThrows
    @Override
    public void distribute() {
        Letter letter = producer.getLetter();
        sender.send(letter);
        atomicInteger.decrementAndGet();
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
