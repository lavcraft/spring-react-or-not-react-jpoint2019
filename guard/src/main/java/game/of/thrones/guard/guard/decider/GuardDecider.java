package game.of.thrones.guard.guard.decider;

import com.naya.speedadjuster.services.LetterRequesterService;
import game.of.thrones.guard.guard.model.Notification;
import game.of.thrones.guard.guard.notifier.Notifier;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GuardDecider {
    private final Notifier                notifier;
    private final Counter                 counter;
    private final ThreadPoolExecutor      letterProcessorExecutor;
    private final LetterRequesterService  letterRequesterService;
    private final BlockingQueue<Runnable> workQueue;

    public GuardDecider(Notifier notifier,
                        MeterRegistry meterRegistry,
                        ThreadPoolExecutor letterProcessorExecutor,
                        LetterRequesterService letterRequesterService) {
        this.notifier = notifier;
        this.letterProcessorExecutor = letterProcessorExecutor;
        this.letterRequesterService = letterRequesterService;

        counter = meterRegistry.counter("letter.rps");
        workQueue = letterProcessorExecutor.getQueue();
    }

    public void decide(Notification notification) {
        letterProcessorExecutor.execute(
                new GuardTask(
                        notification,
                        notifier,
                        counter,
                        letterRequesterService,
                        workQueue
                )
        );
    }

    @Slf4j
    @RequiredArgsConstructor
    public static class GuardTask implements Runnable {
        private final Notification            notification;
        private final Notifier                notifier;
        private final Counter                 counter;
        private final LetterRequesterService  letterRequesterService;
        private final BlockingQueue<Runnable> workQueue;

        @Override
        public void run() {
            String message = "Author of the letter with id:" + notification.getLetterId() + " is " + getDecision();
            notification.setMessage(message);
            notifier.sendNotification(notification);
            counter.increment();

            if(workQueue.size() == 0) {
                letterRequesterService.request(letterRequesterService.getAdjustmentProperties().getLetterProcessorConcurrencyLevel());
            }
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
