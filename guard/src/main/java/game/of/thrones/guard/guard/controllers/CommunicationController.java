package game.of.thrones.guard.guard.controllers;

import game.of.thrones.guard.guard.decider.GuardDecider;
import game.of.thrones.guard.guard.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Evgeny Borisov
 */
@RestController
@RequiredArgsConstructor
public class CommunicationController {
    private final GuardDecider decider;

    @PostMapping("/guard")
    public void updateLetterStatus(@RequestBody Notification notification){ decider.decide(notification);}

}
