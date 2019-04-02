package ru.spring.demo.reactive.smith.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.spring.demo.reactive.smith.decider.GuardDecider;
import ru.spring.demo.reactive.starter.speed.model.DecodedLetter;

@RestController
@RequiredArgsConstructor
public class DecodedLetterController {
    private final GuardDecider decider;

    @PostMapping("/guard")
    public void updateLetterStatus(@RequestBody DecodedLetter decodedLetter) {
        decider.decide(decodedLetter);
    }

}
