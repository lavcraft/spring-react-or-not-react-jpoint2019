package com.naya.gameofthrones.lettergrabber.services;

import com.naya.gameofthrones.lettergrabber.model.Letter;
import com.naya.gameofthrones.lettergrabber.producer.LetterProducer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@Getter
@Service
public class LetterDistributorImpl implements LetterDistributor {
    private final LetterProducer producer;
    private final LetterSender   sender;

    private final AtomicInteger      atomicInteger = new AtomicInteger(0);
    private final Counter            counter;
    private final WebClient.Builder  webClientBuilder;
    private final ThreadPoolExecutor letterProcessorExecutor;

    public LetterDistributorImpl(LetterProducer producer,
                                 LetterSender sender,
                                 MeterRegistry meterRegistry,
                                 WebClient.Builder webClientBuilder,
                                 ThreadPoolExecutor letterProcessorExecutor) {
        this.producer = producer;
        this.sender = sender;
        this.counter = meterRegistry.counter("letterRps");
        this.webClientBuilder = webClientBuilder;
        this.letterProcessorExecutor = letterProcessorExecutor;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        webClientBuilder.baseUrl("http://localhost:8081/").build()
                .post().uri("/analyse/letter")
                .contentType(APPLICATION_STREAM_JSON)
                .accept(APPLICATION_STREAM_JSON)
                .body(
                        producer.letterFlux()
                                .doOnRequest(value -> log.info("request {}", value))
                                .onBackpressureDrop(o -> log.info("Drop {}", o))
                                .doOnNext(letter -> log.debug("produce letter {}", letter)),
                        Letter.class
                )
                .exchange()
                .doOnNext(clientResponse -> log.info("response {}", clientResponse.statusCode()))
                .doOnError(throwable -> log.error("Sth went wrong {}", throwable.getMessage()))
                .retryBackoff(Long.MAX_VALUE, Duration.ofMillis(500))
                .subscribe(aVoid -> log.info("aVoid = " + aVoid));
    }

    //    @Scheduled(fixedDelay = 500)
    public void send() {
        while (true) {
            try {
                if(atomicInteger.get() > 0) {
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
//        sender.send(letter);
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
