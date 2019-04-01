package game.of.thrones.guard.guard.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import game.of.thrones.guard.guard.decider.GuardDecider;
import game.of.thrones.guard.guard.model.Notification;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.SocketAcceptor;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    private Notification getNotification(Payload payload, ObjectMapper mapper) {
        return mapper.readValue(payload.getData().array(), Notification.class);
    }

}
