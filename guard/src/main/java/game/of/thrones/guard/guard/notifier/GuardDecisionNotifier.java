package game.of.thrones.guard.guard.notifier;

import game.of.thrones.guard.guard.model.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service("Status")
@AllArgsConstructor
public class GuardDecisionNotifier implements Notifier {

    RestTemplate restTemplate;

    @Override
    public void sendNotification(Notification notification) {
        try{
            restTemplate.postForObject("http://localhost:8080/letter-status", notification, ResponseEntity.class);
            log.info("Guard notification sent");
        }catch (Exception e) {
            log.error("no sender url found");
        }
    }
}
