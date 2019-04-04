package ru.spring.demo.reactive.starter.speed.rsocket;

import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import io.rsocket.Closeable;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

@SuppressWarnings("unchecked")
public class ReconnectingRSocket extends BaseSubscriber<RSocket> implements RSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReconnectingRSocket.class);

    private final Duration backoff;
    private final Duration backoffMax;

    private volatile     MonoProcessor<RSocket>                                          rSocketMono;
    private static final AtomicReferenceFieldUpdater<ReconnectingRSocket, MonoProcessor> RSOCKET_MONO
            = AtomicReferenceFieldUpdater.newUpdater(ReconnectingRSocket.class, MonoProcessor.class, "rSocketMono");

    public ReconnectingRSocket(Mono<RSocket> rSocketMono, Duration backoff, Duration backoffMax) {
        this.backoff = backoff;
        this.backoffMax = backoffMax;
        this.rSocketMono = MonoProcessor.create();

        rSocketMono.retryBackoff(Long.MAX_VALUE, backoff)
                .repeat()
                .subscribe(this);
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        subscription.request(1);
    }

    @Override
    protected void hookOnNext(RSocket value) {
        LOGGER.info("Connected.");
        value.onClose()
                .subscribe(null, this::reconnect, this::reconnect);
        rSocketMono.onNext(value);
    }

    private void reconnect(Throwable t) {
        LOGGER.error("Error.", t);
        reconnect();
    }

    private void reconnect() {
        LOGGER.info("Reconnecting...");
        request(1);
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        return Mono
                .defer(() -> {
                    MonoProcessor<RSocket> rSocketMono = this.rSocketMono;
                    if(rSocketMono.isSuccess()) {
                        return rSocketMono.peek()
                                .fireAndForget(payload)
                                .doOnError(throwable -> {
                                    if(throwable instanceof ClosedChannelException) {
                                        RSOCKET_MONO.compareAndSet(this, rSocketMono, MonoProcessor.create());
                                    }
                                });
                    } else {
                        return rSocketMono.flatMap(rSocket ->
                                rSocket.fireAndForget(payload)
                                        .doOnError(throwable -> {
                                            if(throwable instanceof ClosedChannelException) {
                                                RSOCKET_MONO.compareAndSet(this, rSocketMono, MonoProcessor.create());
                                            }
                                        })
                        );
                    }
                });
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        return Mono
                .defer(() -> {
                    MonoProcessor<RSocket> rSocketMono = this.rSocketMono;
                    if(rSocketMono.isSuccess()) {
                        return rSocketMono.peek()
                                .requestResponse(payload)
                                .doOnError(throwable -> {
                                    if(throwable instanceof ClosedChannelException) {
                                        RSOCKET_MONO.compareAndSet(this, rSocketMono, MonoProcessor.create());
                                    }
                                });
                    } else {
                        return rSocketMono.flatMap(rSocket ->
                                rSocket.requestResponse(payload)
                                        .doOnError(throwable -> {
                                            if(throwable instanceof ClosedChannelException) {
                                                RSOCKET_MONO.compareAndSet(this, rSocketMono, MonoProcessor.create());
                                            }
                                        })
                        );
                    }
                });
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        return Flux
                .defer(() -> {
                    MonoProcessor<RSocket> rSocketMono = this.rSocketMono;
                    if(rSocketMono.isSuccess()) {
                        return rSocketMono.peek()
                                .requestStream(payload)
                                .doOnError(throwable -> {
                                    if(throwable instanceof ClosedChannelException) {
                                        RSOCKET_MONO.compareAndSet(this, rSocketMono, MonoProcessor.create());
                                    }
                                });
                    } else {
                        return rSocketMono.flatMapMany(rSocket ->
                                rSocket.requestStream(payload)
                                        .doOnError(throwable -> {
                                            if(throwable instanceof ClosedChannelException) {
                                                RSOCKET_MONO.compareAndSet(this, rSocketMono, MonoProcessor.create());
                                            }
                                        })
                        );
                    }
                });
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return Flux
                .defer(() -> {
                    MonoProcessor<RSocket> rSocketMono = this.rSocketMono;
                    if(rSocketMono.isSuccess()) {
                        return rSocketMono.peek()
                                .requestChannel(payloads)
                                .doOnError(throwable -> {
                                    if(throwable instanceof ClosedChannelException) {
                                        RSOCKET_MONO.compareAndSet(this, rSocketMono, MonoProcessor.create());
                                    }
                                });
                    } else {
                        return rSocketMono.flatMapMany(rSocket ->
                                rSocket.requestChannel(payloads)
                                        .doOnError(throwable -> {
                                            if(throwable instanceof ClosedChannelException) {
                                                RSOCKET_MONO.compareAndSet(this, rSocketMono, MonoProcessor.create());
                                            }
                                        })
                        );
                    }
                });
    }

    @Override
    public Mono<Void> metadataPush(Payload payload) {
        return Mono
                .defer(() -> {
                    MonoProcessor<RSocket> rSocketMono = this.rSocketMono;
                    if(rSocketMono.isSuccess()) {
                        return rSocketMono.peek()
                                .metadataPush(payload)
                                .doOnError(throwable -> {
                                    if(throwable instanceof ClosedChannelException) {
                                        RSOCKET_MONO.compareAndSet(this, rSocketMono, MonoProcessor.create());
                                    }
                                });
                    } else {
                        return rSocketMono.flatMap(rSocket ->
                                rSocket.metadataPush(payload)
                                        .doOnError(__ -> RSOCKET_MONO.compareAndSet(this, rSocketMono, MonoProcessor.create()))
                        );
                    }
                });
    }

    @Override
    public double availability() {
        return rSocketMono.isSuccess() ? rSocketMono.peek().availability() : 0d;
    }

    @Override
    public void dispose() {
        super.dispose();
        rSocketMono.dispose();
    }

    @Override
    public boolean isDisposed() {
        return super.isDisposed();
    }

    @Override
    public Mono<Void> onClose() {
        if(rSocketMono.isSuccess()) {
            return rSocketMono.peek().onClose();
        } else {
            return rSocketMono.flatMap(Closeable::onClose);
        }
    }
}
