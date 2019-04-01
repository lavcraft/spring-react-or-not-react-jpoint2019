package com.naya.gameofthrones.lettergrabber.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naya.gameofthrones.lettergrabber.producer.LetterProducer;
import com.naya.speedadjuster.AdjustmentProperties;
import com.naya.speedadjuster.mode.Letter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@Getter
@Service
public class LetterDistributorImpl implements LetterDistributor {
    private final LetterProducer producer;
    private final LetterSender   sender;

    private final AtomicInteger      atomicInteger;
    private final Counter            counter;
    private final WebClient.Builder  webClientBuilder;
    private final ThreadPoolExecutor letterProcessorExecutor;
    private final ObjectMapper       objectMapper;

    private final Mono<RSocket> rSocketMono;

    public LetterDistributorImpl(LetterProducer producer,
                                 LetterSender sender,
                                 MeterRegistry meterRegistry,
                                 WebClient.Builder webClientBuilder,
                                 ThreadPoolExecutor letterProcessorExecutor,
                                 AdjustmentProperties adjustmentProperties,
                                 ObjectMapper objectMapper) {
        this.producer = producer;
        this.sender = sender;
        this.counter = meterRegistry.counter("letter.rps");
        this.webClientBuilder = webClientBuilder;
        this.letterProcessorExecutor = letterProcessorExecutor;
        this.atomicInteger = adjustmentProperties.getRequest();
        this.objectMapper = objectMapper;

        rSocketMono = RSocketFactory.connect()
                .transport(WebsocketClientTransport.create(
                        HttpClient.from(TcpClient.create()
                                .host("localhost")
                                .port(8081)),
                        "/rs"
                ))
                .start();
    }

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        rSocketMono
                .retry()
                .subscribe(rSocket -> rSocket.requestChannel(producer.letterFlux()
                        .log()
//                        .onBackpressureDrop(letter -> log.error("drop {}", letter))
                        .map(letter -> {
                            counter.increment();
                            return DefaultPayload.create(convertToBytes(letter));
                        }))
                        .subscribe(new BaseSubscriber<Payload>() {

                            @Override
                            protected void hookOnSubscribe(Subscription subscription) {
                                subscription.request(3);
                            }

                            @Override
                            protected void hookOnError(Throwable throwable) {
                                log.error("client subscriber error", throwable);
                            }

                            @Override
                            protected void hookOnCancel() {
                                log.info("cancel");
                            }

                            @Override
                            protected void hookOnComplete() {
                                log.info("complete");
                            }

                            @Override
                            protected void hookOnNext(Payload value) {

                            }
                        }));


//        webClientBuilder.baseUrl("http://localhost:8081/").build()
//                .post().uri("/analyse/letter")
//                .contentType(APPLICATION_STREAM_JSON)
//                .accept(APPLICATION_STREAM_JSON)
//                .body(
//                        producer.letterFlux()
//                                .doOnCancel(() -> log.info("Cancel in listener"))
//                                .doOnRequest(value -> log.info("request {}", value))
//                                .onBackpressureBuffer(256)
//                                .doOnCancel(() -> log.info("Cancel in listener after buffer"))
////                                .onBackpressureDrop(o -> log.info("Drop {}", o))
//                                .doOnNext(letter -> log.debug("produce letter {}", letter)),
//                        Letter.class
//                )
//                .exchange()
//                .doOnNext(clientResponse -> log.info("response {}", clientResponse.statusCode()))
//                .doOnError(throwable -> log.error("Sth went wrong {}", throwable.getMessage()))
//                .retryBackoff(Long.MAX_VALUE, Duration.ofMillis(500))
//                .subscribe(aVoid -> log.info("aVoid = " + aVoid));
    }

    @SneakyThrows
    private byte[] convertToBytes(Letter letter) {
        return objectMapper.writeValueAsBytes(letter);
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
