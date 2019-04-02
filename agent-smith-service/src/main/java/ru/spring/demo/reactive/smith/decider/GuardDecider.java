package ru.spring.demo.reactive.smith.decider;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.spring.demo.reactive.smith.notifier.Notifier;
import ru.spring.demo.reactive.starter.speed.AdjustmentProperties;
import ru.spring.demo.reactive.starter.speed.model.DecodedLetter;
import ru.spring.demo.reactive.starter.speed.model.Notification;
import ru.spring.demo.reactive.starter.speed.services.LetterRequesterService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GuardDecider {
    private final AdjustmentProperties    adjustmentProperties;
    private final Notifier                notifier;
    private final Counter                 counter;
    private final ThreadPoolExecutor      letterProcessorExecutor;
    private final LetterRequesterService  letterRequesterService;
    private final BlockingQueue<Runnable> workQueue;

    public GuardDecider(
            AdjustmentProperties adjustmentProperties,
            Notifier notifier,
            MeterRegistry meterRegistry,
            ThreadPoolExecutor letterProcessorExecutor,
            LetterRequesterService letterRequesterService) {
        this.adjustmentProperties = adjustmentProperties;
        this.notifier = notifier;
        this.letterProcessorExecutor = letterProcessorExecutor;
        this.letterRequesterService = letterRequesterService;

        counter = meterRegistry.counter("letter.rps");
        workQueue = letterProcessorExecutor.getQueue();
    }

    public void decide(DecodedLetter notification) {
        letterProcessorExecutor.execute(
                getCommand(notification)
        );
    }

    private GuardTask getCommand(DecodedLetter notification) {
        return new GuardTask(
                adjustmentProperties,
                notification,
                notifier,
                counter,
                letterRequesterService,
                workQueue
        );
    }

    public Mono<Void> decideDeferred(DecodedLetter decodedLetter) {
        return Mono.<Void>fromRunnable(getCommand(decodedLetter))
                .subscribeOn(Schedulers.fromExecutor(letterProcessorExecutor));
    }

    @Slf4j
    @RequiredArgsConstructor
    public static class GuardTask implements Runnable {
        private final AdjustmentProperties    adjustmentProperties;
        private final DecodedLetter           decodedLetter;
        private final Notifier                notifier;
        private final Counter                 counter;
        private final LetterRequesterService  letterRequesterService;
        private final BlockingQueue<Runnable> workQueue;

        @SneakyThrows
        private String getDecision() {
            TimeUnit.MILLISECONDS.sleep(adjustmentProperties.getProcessingTime());
            int decision = (int) ((Math.random() * (2)) + 1);
            if(decision == 1) {
                return "Nothing";
            } else {
                return "Block";
            }
        }

        @Override
        public void run() {
            String decision = getDecision();

            Notification notification = Notification.builder()
                    .author(decodedLetter.getAuthor())
                    .action(decision)
                    .build();

            notifier.sendNotification(notification);
            counter.increment();
            if(workQueue.size() == 0) {
                letterRequesterService.request(letterRequesterService.getAdjustmentProperties().getLetterProcessorConcurrencyLevel());
            }
        }
    }
}
