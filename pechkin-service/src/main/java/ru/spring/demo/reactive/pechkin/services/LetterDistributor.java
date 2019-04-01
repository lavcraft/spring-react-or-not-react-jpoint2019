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
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import ru.spring.demo.reactive.pechkin.producer.LetterProducer;
import ru.spring.demo.reactive.starter.speed.model.Letter;

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
        bigBrotherRSocketMono
                .retry()
                .subscribe(rSocket -> rSocket.requestChannel(
                        producer.letterFlux()
//                        .onBackpressureDrop(letter -> log.error("drop {}", letter))
                                .log()
                                .doOnNext(payload -> counter.increment())
                                .map(letter -> DefaultPayload.create(convertToBytes(letter)))
                ).subscribe());
    }

    @SneakyThrows
    private byte[] convertToBytes(Letter letter) {
        return objectMapper.writeValueAsBytes(letter);
    }

}
