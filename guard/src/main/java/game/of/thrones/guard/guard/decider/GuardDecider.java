package game.of.thrones.guard.guard.decider;

import game.of.thrones.guard.guard.model.Notification;
import game.of.thrones.guard.guard.notifier.Notifier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GuardDecider {

    private Notifier notifier;

    public void decide(Notification notification) {
        log.info("Received from decoder: " + notification);
        String message = "Author of the letter with id:" + notification.getLetterId() + " is " + getDecision();
        notification.setMessage(message);
        notifier.sendNotification(notification);
    }

    private String getDecision(){
        int decision = (int) ((Math.random() * (2)) + 1);
        log.info(String.valueOf(decision));
        if( decision == 1){
            return "dangerous, We send a squad of guards to you......hold him!!!";
        }else {
            return "not dangerous. But you can kill him just because it is Game of Thrones!";
        }
    }
}
