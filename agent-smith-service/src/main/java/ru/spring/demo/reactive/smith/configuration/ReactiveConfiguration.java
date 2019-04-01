package ru.spring.demo.reactive.smith.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.SocketAcceptor;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.spring.demo.reactive.smith.decider.GuardDecider;
import ru.spring.demo.reactive.starter.speed.model.DecodedLetter;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ReactiveConfiguration {

    @Bean
    public SocketAcceptor socketAcceptor(
            GuardDecider guardDecider,
            ObjectMapper mapper,
            ThreadPoolExecutor threadPoolExecutor
    ) {
        return (setup, sendingSocket) -> Mono.just(new AbstractRSocket() {
            @Override
            public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
                return Flux.from(payloads)
                        .map(payload -> getNotification(payload, mapper))
                        .flatMap(guardDecider::decideDeferred,
                                threadPoolExecutor.getMaximumPoolSize()
                        )
                        .log()
                        .thenMany(Flux.empty());
            }
        });
    }

    @SneakyThrows
    private DecodedLetter getNotification(Payload payload, ObjectMapper mapper) {
        return mapper.readValue(payload.getData().array(), DecodedLetter.class);
    }

}
