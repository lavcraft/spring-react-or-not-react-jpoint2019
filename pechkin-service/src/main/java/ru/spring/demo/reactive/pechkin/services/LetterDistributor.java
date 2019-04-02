package ru.spring.demo.reactive.pechkin.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.spring.demo.reactive.pechkin.producer.LetterProducer;
import ru.spring.demo.reactive.starter.speed.AdjustmentProperties;
import ru.spring.demo.reactive.starter.speed.model.Letter;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@Getter
@Service
public class LetterDistributor {
    private final LetterSender sender;
    private final AdjustmentProperties adjustmentProperties;
    private final LetterProducer       producer;
    private final Counter              counter;
    private final ThreadPoolExecutor   letterProcessorExecutor;
    private final ObjectMapper         objectMapper;

    public LetterDistributor(
            LetterSender sender,
            AdjustmentProperties adjustmentProperties,
            LetterProducer producer,
            MeterRegistry meterRegistry,
            ThreadPoolExecutor letterProcessorExecutor,
            ObjectMapper objectMapper) {
        this.sender = sender;
        this.adjustmentProperties = adjustmentProperties;
        this.producer = producer;
        this.counter = meterRegistry.counter("letter.rps");
        this.letterProcessorExecutor = letterProcessorExecutor;
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        while (true) {
            try {
                if(adjustmentProperties.getRequest().get() > 0) {
                    distribute();
                    counter.increment();
                } else {
                    TimeUnit.MILLISECONDS.sleep(50);
                }
            } catch (Exception e) {
                log.error("Cannot send letter");
                try {
                    TimeUnit.MILLISECONDS.sleep(adjustmentProperties.getProcessingTime());
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    @SneakyThrows
    public void distribute() {
        Letter letter = producer.getLetter();
        log.info("letter = " + letter);
        sender.send(letter);
        adjustmentProperties.getRequest().getAndDecrement();
    }


//        @EventListener(ApplicationStartedEvent.class)
//        public void init () {
//        bigBrotherRSocketMono
//                .retry()
//                .subscribe(rSocket -> rSocket.requestChannel(
//                        producer.letterFlux()
////                        .onBackpressureDrop(letter -> log.error("drop {}", letter))
//                                .log()
//                                .doOnNext(payload -> counter.increment())
//                                .map(letter -> DefaultPayload.create(convertToBytes(letter)))
//                ).subscribe());

//        webClientBuilder.baseUrl("http://localhost:8081/").build()
//                .post().uri("/analyse/letter")
//                .contentType(MediaType.APPLICATION_STREAM_JSON)
//                .accept(MediaType.APPLICATION_STREAM_JSON)
//                .body(
//                        producer.letterFlux()
//                                .doOnRequest(value -> log.info("request {}", value))
////                                .onBackpressureBuffer(256)
//                                .doOnNext(letter -> counter.increment())
////                                .onBackpressureDrop(o -> log.info("Drop {}", o))
//                                .doOnNext(letter -> log.debug("produce letter {}", letter))
//                                .log(),
//                        Letter.class
//                )
//                .exchange()
//                .doOnError(throwable -> log.error("Sth went wrong {}", throwable.getMessage()))
//                .retryBackoff(Long.MAX_VALUE, Duration.ofMillis(500))
//                .log()
//                .subscribe(aVoid -> log.info("aVoid = " + aVoid));
//        }

}
