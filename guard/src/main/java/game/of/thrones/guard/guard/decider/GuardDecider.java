package game.of.thrones.guard.guard.decider;

import game.of.thrones.guard.guard.model.Notification;
import game.of.thrones.guard.guard.notifier.Notifier;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GuardDecider {
    private final Notifier notifier;
    private final Counter  counter;
    private final ThreadPoolExecutor letterProcessorExecutor;

    public GuardDecider(Notifier notifier,
                        MeterRegistry meterRegistry,
                        ThreadPoolExecutor letterProcessorExecutor) {
        this.notifier = notifier;
        this.counter = meterRegistry.counter("letter.rps");
        this.letterProcessorExecutor = letterProcessorExecutor;
    }

    public void decide(Notification notification) {
        letterProcessorExecutor.execute(
                new GuardTask(notification, notifier, counter)
        );
    }

    @Slf4j
    @RequiredArgsConstructor
    public static class GuardTask implements Runnable {
        private final Notification notification;
        private final Notifier notifier;
        private final Counter  counter;

        @Override
        public void run() {
            String message = "Author of the letter with id:" + notification.getLetterId() + " is " + getDecision();
            notification.setMessage(message);
            notifier.sendNotification(notification);
            counter.increment();
        }

        @SneakyThrows
        private static String getDecision() {
            TimeUnit.SECONDS.sleep(1);
            int decision = (int) ((Math.random() * (2)) + 1);
            if(decision == 1) {
                return "dangerous, We send a squad of guards to you......hold him!!!";
            } else {
                return "not dangerous. But you can kill him just because it is Game of Thrones!";
            }
        }
    }
}
