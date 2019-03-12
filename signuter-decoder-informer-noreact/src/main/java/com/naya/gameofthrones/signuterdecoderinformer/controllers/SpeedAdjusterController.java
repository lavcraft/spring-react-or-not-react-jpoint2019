package com.naya.gameofthrones.signuterdecoderinformer.controllers;

import com.naya.speedadjuster.AdjustmentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SpeedAdjusterController {
    private final AdjustmentProperties adjustmentProperties;

    @GetMapping("/speed/{level}")
    public String setSpeed(@PathVariable int level) {
        adjustmentProperties.setSlowMultiplier(level);

        return "{ \"status\": \"ok\"}";
    }
}
