package com.naya.gameofthrones.lettergrabber.controllers;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FeedbackController {

    @PostMapping("/letter-status")
    public void feedback(@RequestBody Feedback feedback) {
        log.info("feedback = " + feedback);
    }

    @ToString
    private static class Feedback {
        private String letterId;
        private String message;
    }
}
