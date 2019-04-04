package ru.spring.demo.reactive.pechkin.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import ru.spring.demo.reactive.pechkin.producer.LetterProducer;
import ru.spring.demo.reactive.starter.speed.model.Letter;
import ru.spring.demo.reactive.starter.speed.rsocket.ReconnectingRSocket;

import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Evgeny Borisov
 */
@Slf4j
@Getter
@Service
public class LetterDistributor {
    private final LetterProducer     producer;
    private final Counter            counter;
    private final ThreadPoolExecutor letterProcessorExecutor;
    private final ObjectMapper       objectMapper;

    private final Mono<RSocket> bigBrotherRSocketMono;

    public LetterDistributor(LetterProducer producer,
                             MeterRegistry meterRegistry,
                             ThreadPoolExecutor letterProcessorExecutor,
                             ObjectMapper objectMapper) {
        this.producer = producer;
        this.counter = meterRegistry.counter("letter.rps");
        this.letterProcessorExecutor = letterProcessorExecutor;
        this.objectMapper = objectMapper;

        bigBrotherRSocketMono = RSocketFactory.connect()
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
        new ReconnectingRSocket(bigBrotherRSocketMono, Duration.ofMillis(200), Duration.ofMillis(1000))
                .requestChannel(
                        Flux.defer(() -> producer.letterFlux()
                                .log()
                                .doOnNext(payload -> counter.increment())
                                .map(letter -> DefaultPayload.create(convertToBytes(letter))))
                )
                .doOnError(e -> log.error("Got App Error ", e))
                .retryBackoff(Integer.MAX_VALUE, Duration.ofMillis(200), Duration.ofMillis(1000))
                .subscribe();
    }

    @SneakyThrows
    private byte[] convertToBytes(Letter letter) {
        return objectMapper.writeValueAsBytes(letter);
    }

}
