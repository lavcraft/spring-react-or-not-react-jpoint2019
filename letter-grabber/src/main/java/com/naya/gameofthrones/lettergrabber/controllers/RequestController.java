package com.naya.gameofthrones.lettergrabber.controllers;

import com.naya.gameofthrones.lettergrabber.services.LetterDistributor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RequestController {
    private final LetterDistributor letterDistributor;

    @GetMapping("/request/{request}")
    public void request(@PathVariable int request) {
        letterDistributor.request(request);
    }

    @GetMapping("/request")
    public int getAtomicInteger() {
        return letterDistributor.getRemainingRequestCount();
    }

}
